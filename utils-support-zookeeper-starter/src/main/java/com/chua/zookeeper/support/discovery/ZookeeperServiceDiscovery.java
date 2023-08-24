package com.chua.zookeeper.support.discovery;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.context.annotation.AutoInject;
import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.discovery.DiscoveryBoundType;
import com.chua.common.support.discovery.DiscoveryOption;
import com.chua.common.support.discovery.ServiceDiscovery;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.RandomRoundRobin;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * 服务发现
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
@Slf4j
@Spi("zookeeper")
public class ZookeeperServiceDiscovery implements ServiceDiscovery, CuratorCacheListener {
    private static final String PROXY_NODE = "/service";
    private final CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
    private final AtomicBoolean state = new AtomicBoolean(false);
    private final Multimap<String, Discovery> nodeCache = HashMultimap.create();
    private String root = PROXY_NODE;
    private CuratorFramework curatorFramework;
    @AutoInject
    private Robin robin = new RandomRoundRobin();

    @Override
    public ServiceDiscovery robin(Robin robin) {
        this.robin = robin;
        return this;
    }

    @Override
    public Discovery discovery(String discovery, DiscoveryBoundType strategy) throws Exception {
        discovery = StringUtils.startWithAppend(discovery, "/");
        List<Discovery> proxyUri = new LinkedList<>();
        Collection<Discovery> strings = nodeCache.get(discovery);
        if (null != strings) {
            proxyUri.addAll(strings);
        }

        String proxyPath = discovery;
        if (proxyUri.isEmpty()) {
            String fullPath = discovery;
            while (!Strings.isNullOrEmpty(fullPath = (FileUtils.getFullPath(fullPath)))) {
                if (fullPath.endsWith("/")) {
                    fullPath = fullPath.substring(0, fullPath.length() - 1);
                }

                Collection<Discovery> strings1 = nodeCache.get(fullPath);

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
    public ServiceDiscovery register(Discovery discovery) {
        try {
            nodeCache.put(discovery.getDiscovery(), discovery);
            String newNode = root + discovery.getDiscovery() + "/" + URLEncoder.encode(discovery.getAddress(), "UTF-8");
            if (null == this.curatorFramework.checkExists().forPath(newNode)) {
                this.curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
                        .forPath(newNode, JSON.toJSONBytes(discovery));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public ServiceDiscovery start(DiscoveryOption discoveryOption) throws Exception {
        try {
            if (!Strings.isNullOrEmpty(discoveryOption.getRoot())) {
                this.root = discoveryOption.getRoot();
            }

            CountDownLatch countDownLatch = new CountDownLatch(1);
            builder.connectString(discoveryOption.getAddress());
            this.curatorFramework = builder.retryPolicy(new RetryNTimes(3, 1000)).build();
            if (curatorFramework.getState() == CuratorFrameworkState.STARTED) {
                state.set(true);
                return this;
            }

            this.curatorFramework.start();
            this.curatorFramework.getConnectionStateListenable().addListener((client, newState) -> {
                log.info("Zookeeper waiting for connection");
                state.set(newState.isConnected());
                if (newState.isConnected()) {
                    log.info("Zookeeper connection succeeded...");
                    countDownLatch.countDown();
                }
            });

            if (curatorFramework.getState() != CuratorFrameworkState.STARTED) {
                try {
                    boolean await = countDownLatch.await(10, TimeUnit.SECONDS);
                    if (!await) {
                        stop();
                        return this;
                    }
                    log.info(">>>>>>>>>>> ZookeeperFactory connection complete.");
                    state.set(true);

                } catch (Exception e) {
                    e.printStackTrace();
                    log.info(">>>>>>>>>>> ZookeeperFactory connection activation failed.");
                }
            } else {
                state.set(true);
            }
        } finally {
            initialMonitor();
        }

        return this;
    }

    private void initialMonitor() {
        CuratorCache curatorCache = CuratorCache.builder(curatorFramework, root).build();
        curatorCache.start();
        curatorCache.listenable().addListener(this);
    }


    @Override
    public ServiceDiscovery stop() throws Exception {
        this.curatorFramework.close();
        return this;
    }

    @Override
    public void event(Type type, ChildData childData, ChildData childData1) {
        if (type == Type.NODE_CREATED) {
            doCreated(childData1);
            return;
        }

        if (type == Type.NODE_DELETED) {
            doDeleted(childData);
        }
    }


    private void doDeleted(ChildData childData) {
        String replace = childData.getPath().replace(root, "");
        if (Strings.isNullOrEmpty(replace)) {
            return;
        }

        String[] path = replace.substring(1).split("/", 2);
        if (path.length != 2) {
            return;
        }

        String proxyUrl = URLDecoder.decode(path[1]);
        String key = "/" + path[0];
        Collection<Discovery> discoveries = nodeCache.get(key);
        if (null == discoveries) {
            return;
        }

        List<Discovery> cache = new LinkedList<>();
        for (Discovery urlAddress : discoveries) {
            if (urlAddress.getDiscovery().equals(proxyUrl)) {
                cache.add(urlAddress);
            }
        }

        for (Discovery urlAddress : cache) {
            nodeCache.remove(key, urlAddress);
        }
    }

    private void doCreated(ChildData childData1) {
        String replace = childData1.getPath().replace(root, "");
        if (Strings.isNullOrEmpty(replace)) {
            return;
        }

        String[] path = replace.substring(1).split("/", 2);
        if (path.length != 2) {
            return;
        }

        String proxyUrl = URLDecoder.decode(path[1]);
        Discovery urlAddress = Discovery.builder().discovery(proxyUrl).build();
        refreshUrlAddress(childData1.getData(), urlAddress);
        nodeCache.put("/" + path[0], urlAddress);
    }


    private void refreshUrlAddress(byte[] bytes, Discovery discovery) {
        if (null != bytes && bytes.length > 0) {
            JSONObject jsonObject = null;
            try {
                jsonObject = JSON.parseObject(new String(bytes, UTF_8));
                BeanUtils.copyProperties(jsonObject, discovery);
            } catch (Throwable ignored) {
            }
        }
    }
}
