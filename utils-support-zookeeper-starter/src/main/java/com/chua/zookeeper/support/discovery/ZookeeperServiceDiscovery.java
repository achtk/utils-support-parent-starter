package com.chua.zookeeper.support.discovery;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.discovery.AbstractServiceDiscovery;
import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.discovery.DiscoveryOption;
import com.chua.common.support.discovery.ServiceDiscovery;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 服务发现
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
@Slf4j
@Spi("zookeeper")
public class ZookeeperServiceDiscovery extends AbstractServiceDiscovery implements CuratorCacheListener {
    private CuratorFramework curatorFramework;
    private final AtomicBoolean state = new AtomicBoolean(false);
    private final CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
    private final ConcurrentMap<String, List<Discovery>> received = new ConcurrentHashMap<>();

    private String root;

    public ZookeeperServiceDiscovery(DiscoveryOption discoveryOption) {
        super(discoveryOption);
    }

    @Override
    public ServiceDiscovery registerService(String path, Discovery discovery) {
        String newNode = root + "/" + path;
        try {
            if (null == this.curatorFramework.checkExists().forPath(newNode)) {
                this.curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
                        .forPath(newNode, JSON.toJSONBytes(discovery));
            }
            received.computeIfAbsent(path, it -> new LinkedList<>()).add(discovery);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public Discovery getService(String path, String balance) {
        List<Discovery> netAddresses = received.get(path);
        Robin robin = ServiceProvider.of(Robin.class).getNewExtension(balance);
        Robin robin1 = robin.create();
        robin1.addNode(netAddresses);
        Node selectNode = robin1.selectNode();
        return selectNode.getValue(Discovery.class);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void start() throws IOException {
        if (state.get()) {
            log.info("已启动");
            return;
        }
        state.set(true);
        if (!StringUtils.isNullOrEmpty(discoveryOption.getRoot())) {
            this.root = StringUtils.startWithAppend(discoveryOption.getRoot(), "/");
        } else {
            this.root = "/discovery";
        }

        CountDownLatch countDownLatch = new CountDownLatch(1);
        builder.connectString(discoveryOption.getAddress());
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
        initialMonitor();

    }
    private void initialMonitor() {
        CuratorCache curatorCache = CuratorCache.builder(curatorFramework, root).build();
        curatorCache.start();
        curatorCache.listenable().addListener(this);
    }

    @Override
    public void close() throws IOException {
        curatorFramework.close();
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
        if (StringUtils.isNullOrEmpty(replace)) {
            return;
        }

        String[] path = replace.substring(1).split("/", 2);
        if (path.length != 2) {
            return;
        }

        String proxyUrl = URLDecoder.decode(path[1]);
        String key = "/" + path[0];
        List<Discovery> discoveries = received.get(key);
        if (null == discoveries) {
            return;
        }

        List<Discovery> cache = new LinkedList<>();
        for (Discovery discovery : discoveries) {
            if (discovery.getUriSpec().equals(proxyUrl)) {
                cache.add(discovery);
            }
        }

        for (Discovery discovery : cache) {
            discoveries.remove(discovery);
        }
    }

    private void doCreated(ChildData childData1) {
        String replace = childData1.getPath().replace(root, "");
        if (StringUtils.isNullOrEmpty(replace)) {
            return;
        }

        String[] path = replace.substring(1).split("/", 2);
        if (path.length != 2) {
            return;
        }

        String proxyUrl = URLDecoder.decode(path[1]);
        Discovery discovery = JSON.parseObject(childData1.getData(), Discovery.class);
        refreshUrlAddress(childData1.getData(), discovery);
        received.computeIfAbsent("/" + path[0], it -> new LinkedList<>()).add(discovery);
    }


    private void refreshUrlAddress(byte[] bytes, Discovery discovery) {
        if (null != bytes && bytes.length > 0) {
            JSONObject jsonObject = null;
            try {
                jsonObject = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8));
                BeanUtils.copyProperties(jsonObject, discovery);
            } catch (Throwable ignored) {
            }
        }
    }
}
