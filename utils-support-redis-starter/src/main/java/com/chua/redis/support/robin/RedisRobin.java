package com.chua.redis.support.robin;

import com.alibaba.fastjson2.JSON;
import com.chua.common.support.collection.MultiLinkedValueMap;
import com.chua.common.support.collection.MultiValueMap;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.lang.robin.RobinConfig;
import com.chua.common.support.lang.robin.WeightedRoundRobin;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * redis
 *
 * @author CH
 */
public class RedisRobin implements Robin, Runnable {

    private final String root;
    private final JedisPool jedisPool;

    private final Robin robin = new WeightedRoundRobin();
    private final RobinConfig config;
    private final ScheduledExecutorService executor = ThreadUtils.newScheduledThreadPoolExecutor("redis-service-discovery");

    public RedisRobin(RobinConfig config) {
        this.config = config;
        this.root = config.getRoot();
        jedisPool = new JedisPool(config.getHost(), config.getPort(), config.getUser(), config.getPassword());
        executor.schedule(this, 4, TimeUnit.SECONDS);
    }

    @Override
    public Node selectNode() {
        List<Node> proxyUri = new LinkedList<>();
        MultiValueMap<String, Node> cache = new MultiLinkedValueMap<>();
        try (Jedis resource = jedisPool.getResource()) {
            Set<String> keys = resource.keys(root + ":*");
            for (String key : keys) {
                cache.put(root, JSON.parseObject(resource.get(key), Node.class));
            }

            Collection<Node> strings = cache.get(root);
            if (null != strings) {
                proxyUri.addAll(strings);
            }
        }
        String proxyPath = root;
        if (proxyUri.isEmpty()) {
            String fullPath = root;
            while (!StringUtils.isNullOrEmpty(fullPath = (FileUtils.getFullPath(fullPath)))) {
                if (fullPath.endsWith("/")) {
                    fullPath = fullPath.substring(0, fullPath.length() - 1);
                }

                Collection<Node> strings1 = cache.get(fullPath);

                proxyPath = fullPath;
                if (!strings1.isEmpty()) {
                    proxyUri.addAll(strings1);
                    break;
                }
            }
        }

        Robin robin1 = robin.create();
        robin1.addNodes(proxyUri);
        return robin1.selectNode();
    }

    @Override
    public Robin create() {
        return new RedisRobin(config);
    }

    @Override
    public Robin clear() {
        try (Jedis resource = jedisPool.getResource()) {
            Set<String> keys = resource.keys(root + ":*");
            for (String key : keys) {
                resource.del(key);
            }
        }
        return new RedisRobin(config);
    }

    @Override
    public Robin addNode(Node node) {
        try (Jedis resource = jedisPool.getResource()) {
            resource.setex(root + ":" + resource.keys(root + ":*").size() + 1, 10, JSON.toJSONString(node));
        }
        return this;
    }

    @Override
    public void close() throws Exception {
        IoUtils.closeQuietly(jedisPool);
        try {
            executor.shutdownNow();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void run() {
        try (Jedis resource = jedisPool.getResource()) {
            Set<String> keys = resource.keys(root + ":*");
            for (String key : keys) {
                resource.expire(key, 10);
            }
        }
    }
}
