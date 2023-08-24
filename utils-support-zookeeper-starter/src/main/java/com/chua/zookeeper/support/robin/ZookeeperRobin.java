package com.chua.zookeeper.support.robin;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.lang.robin.RobinConfig;
import com.chua.common.support.lang.robin.WeightedRoundRobin;
import com.chua.common.support.utils.FileUtils;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * zk
 *
 * @author CH
 */
@Slf4j
public class ZookeeperRobin implements Robin, CuratorCacheListener, InitializingAware {
    private final Multimap<String, Node> nodeCache = HashMultimap.create();
    private final String root;
    private final Robin robin = new WeightedRoundRobin();
    private final RobinConfig config;
    private final CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
    private final AtomicBoolean state = new AtomicBoolean(false);

    private CuratorFramework curatorFramework;


    public ZookeeperRobin(RobinConfig config) {
        this.config = config;
        this.root = config.getRoot();
        afterPropertiesSet();
    }

    @Override
    public Node selectNode() {
        List<Node> proxyUri = new LinkedList<>();
        Collection<Node> strings = nodeCache.get(root);
        if (null != strings) {
            proxyUri.addAll(strings);
        }

        String proxyPath = root;
        if (proxyUri.isEmpty()) {
            String fullPath = root;
            while (!Strings.isNullOrEmpty(fullPath = (FileUtils.getFullPath(fullPath)))) {
                if (fullPath.endsWith("/")) {
                    fullPath = fullPath.substring(0, fullPath.length() - 1);
                }

                Collection<Node> strings1 = nodeCache.get(fullPath);

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
        return this;
    }

    @Override
    public Robin clear() {
        try {
            this.curatorFramework.delete().forPath(root);
        } catch (Exception ignored) {
        }
        return this;
    }

    @Override
    public Robin addNode(Node node) {
        try {
            String newNode = root;
            if (null == this.curatorFramework.checkExists().forPath(newNode)) {
                this.curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
                        .forPath(newNode, JSON.toJSONBytes(node));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            builder.connectString(config.getHost() + ":" + config.getPort());
            this.curatorFramework = builder.retryPolicy(new RetryNTimes(3, 1000)).build();
            if (curatorFramework.getState() == CuratorFrameworkState.STARTED) {
                state.set(true);
                return;
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
                        close();
                        return;
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

    }

    private void initialMonitor() {
        CuratorCache curatorCache = CuratorCache.builder(curatorFramework, root).build();
        curatorCache.start();
        curatorCache.listenable().addListener(this);
    }

    @Override
    public void close() throws Exception {
        this.curatorFramework.close();
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
        Collection<Node> discoveries = nodeCache.get(key);
        List<Node> cache = new LinkedList<>();
        for (Node urlAddress : discoveries) {
            if (urlAddress.getContent().equals(proxyUrl)) {
                cache.add(urlAddress);
            }
        }

        for (Node urlAddress : cache) {
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

        String proxyUrl = null;
        try {
            proxyUrl = URLDecoder.decode(path[1], "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        Node urlAddress = new Node(proxyUrl);
        refreshUrlAddress(childData1.getData(), urlAddress);
        nodeCache.put("/" + path[0], urlAddress);
    }


    private void refreshUrlAddress(byte[] bytes, Node node) {
        if (null != bytes && bytes.length > 0) {
            JSONObject jsonObject = null;
            try {
                jsonObject = JSON.parseObject(new String(bytes, UTF_8));
                BeanUtils.copyProperties(jsonObject, node);
            } catch (Throwable ignored) {
            }
        }
    }
}
