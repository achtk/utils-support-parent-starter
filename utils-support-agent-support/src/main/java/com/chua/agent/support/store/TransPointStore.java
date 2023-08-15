package com.chua.agent.support.store;

import com.chua.agent.support.thread.DefaultThreadFactory;
import com.chua.agent.support.transpoint.MqTransPoint;
import com.chua.agent.support.transpoint.TransPoint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.chua.agent.support.store.AgentStore.getStringValue;

/**
 * 数据传输缓存
 *
 * @author CH
 */
public class TransPointStore implements TransPoint {

    private static TransPoint transPoint;

    private static final int thread = Runtime.getRuntime().availableProcessors() * 2;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(thread, new DefaultThreadFactory("thread-agent-embed"));

    /**
     * 初始化传输器
     */
    public static void installTransPoint() {
        String mq = getStringValue(TRANS_SERVER_TYPE, "MQ");
        if ("MQ".equalsIgnoreCase(mq)) {
            TransPointStore.transPoint = new MqTransPoint();
        }

        if (null != transPoint) {
            EXECUTOR_SERVICE.execute(() -> {
                transPoint.connect();
            });
        }
    }

    @Override
    public void connect() {
        if (null == transPoint) {
            return;
        }
        transPoint.connect();
    }

    @Override
    public void publish(String message) {
        if (null == transPoint) {
            return;
        }
        transPoint.publish(message);
    }
}
