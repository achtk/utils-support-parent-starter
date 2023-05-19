package com.chua.zookeeper.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author CH
 */
@Slf4j
final class ZookeeperClient extends AbstractClient<CuratorFramework> {
    private final CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
    private final AtomicBoolean state = new AtomicBoolean(false);
    private CuratorFramework curatorFramework;
    private Executor executor;

    ZookeeperClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public CuratorFramework getClient() {
        return curatorFramework;
    }

    @Override
    public void closeClient(CuratorFramework client) {
        client.close();
    }

    @Override
    public void connectClient() {
        builder.connectString(url);
        this.curatorFramework = builder.build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
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
    }

    @Override
    public void close() {
        if (null != curatorFramework) {
            curatorFramework.close();
            curatorFramework = null;
        }
        ThreadUtils.closeQuietly(executor);
    }

    @Override
    public void afterPropertiesSet() {
        int connectionTimeoutMillis = clientOption.connectionTimeoutMillis();
        int sessionTimeoutMillis = clientOption.sessionTimeoutMillis();
        this.executor = clientOption.executor();
        int retry = clientOption.retry();

        if (sessionTimeoutMillis > 0) {
            builder.sessionTimeoutMs(sessionTimeoutMillis);
        }

        if (connectionTimeoutMillis > 0) {
            builder.connectionTimeoutMs(connectionTimeoutMillis);
        }

        if (retry > 0) {
            builder.retryPolicy(new RetryNTimes(retry, 1000));
        }
    }


}
