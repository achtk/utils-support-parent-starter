package com.chua.redis.support.discovery;

import com.alibaba.fastjson2.JSON;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.MultiLinkedValueMap;
import com.chua.common.support.collection.MultiValueMap;
import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.discovery.DiscoveryBoundType;
import com.chua.common.support.discovery.DiscoveryOption;
import com.chua.common.support.discovery.ServiceDiscovery;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.RandomRoundRobin;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.net.NetAddress;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis
 *
 * @author CH
 */
@Spi("redis")
public class RedisServiceDiscovery implements ServiceDiscovery, Runnable {
    private static final String PROXY_NODE = "service";
    private final long ex = 115;
    private final GenericObjectPoolConfig<Jedis> config = new GenericObjectPoolConfig<>();

    private final ScheduledExecutorService executor = ThreadUtils.newScheduledThreadPoolExecutor("redis-service-discovery");
    private final Map<String, Discovery> cache = new ConcurrentHashMap<>();
    private JedisPool jedisPool;
    private String root = PROXY_NODE;
    private Robin robin = new RandomRoundRobin();

    {
        executor.schedule(this, 3, TimeUnit.SECONDS);
    }

    @Override
    public ServiceDiscovery robin(Robin robin) {
        this.robin = robin;
        return this;
    }


    @Override
    public Discovery discovery(String discovery, DiscoveryBoundType strategy) throws Exception {
        discovery = root + StringUtils.startWithAppend(discovery, "/");
        List<Discovery> proxyUri = new LinkedList<>();
        MultiValueMap<String, Discovery> cache = new MultiLinkedValueMap<>();
        try (Jedis resource = jedisPool.getResource()) {
            Set<String> keys = resource.keys(discovery + ":*");
            for (String key : keys) {
                cache.put(discovery, JSON.parseObject(resource.get(key), Discovery.class));
            }

            Collection<Discovery> strings = cache.get(discovery);
            if (null != strings) {
                proxyUri.addAll(strings);
            }
        }

        String proxyPath = discovery;
        if (proxyUri.isEmpty()) {
            String fullPath = discovery;
            while (!StringUtils.isNullOrEmpty(fullPath = (FileUtils.getFullPath(fullPath)))) {
                if (fullPath.endsWith("/")) {
                    fullPath = fullPath.substring(0, fullPath.length() - 1);
                }

                Collection<Discovery> strings1 = cache.get(fullPath);

                proxyPath = fullPath;
                if (!strings1.isEmpty()) {
                    proxyUri.addAll(strings1);
                    break;
                }
            }
        }

        Robin robin1 = robin.create();
        robin1.addNodes(createNode(proxyUri, discovery.substring(proxyPath.length())));
        Node discoveryNode = robin1.selectNode();
        return null == discoveryNode ? null : discoveryNode.getValue(Discovery.class);
    }

    @Override
    public ServiceDiscovery register(Discovery discovery) {
        String node = null;
        try {
            node = root + discovery.getDiscovery() + ":" + URLEncoder.encode(discovery.getAddress(), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        try (Jedis resource = jedisPool.getResource()) {
            resource.setex(node, ex, JSON.toJSONString(discovery));
        }
        return this;
    }


    /**
     * 创建节点
     *
     * @param proxyUri url
     * @param uri      uri
     * @return node
     */
    private List<Node> createNode(List<Discovery> proxyUri, String uri) {
        return proxyUri.stream().map(it -> {
            Node node = new Node();
            node.setContent(it);
            node.setWeight((int) it.getWeight());
            return node;
        }).collect(Collectors.toList());
    }

    @Override
    public ServiceDiscovery start(DiscoveryOption discoveryOption) throws Exception {
        this.root = StringUtils.defaultString(discoveryOption.getRoot(), PROXY_NODE);
        NetAddress netAddress = NetAddress.of(discoveryOption.getAddress());
        jedisPool = new JedisPool(config, netAddress.getHost(), netAddress.getPort(), discoveryOption.getConnectionTimeoutMillis(), discoveryOption.getUser(), discoveryOption.getPassword(), NumberUtils.toInt(discoveryOption.getDatabase(), 10));
        return this;
    }

    @Override
    public ServiceDiscovery stop() throws Exception {
        executor.shutdownNow();
        return this;
    }

    @Override
    public void run() {
        for (Map.Entry<String, Discovery> entry : cache.entrySet()) {
            try (Jedis resource = jedisPool.getResource()) {
                resource.setex(entry.getKey(), ex, JSON.toJSONString(entry.getValue()));
            }
        }
    }
}
