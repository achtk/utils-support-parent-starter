package com.chua.agent.support.store;

import com.chua.agent.support.thread.DefaultThreadFactory;
import com.chua.agent.support.transpoint.MqTransPoint;
import com.chua.agent.support.transpoint.TransPoint;
import com.chua.agent.support.utils.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.chua.agent.support.store.AgentStore.*;

/**
 * 数据传输缓存
 *
 * @author CH
 */
public class TransPointStore implements TransPoint {

    private static TransPoint transPoint;

    public static final TransPointStore INSTANCE = new TransPointStore();

    private static final int thread = Runtime.getRuntime().availableProcessors() * 2;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(thread, new DefaultThreadFactory("thread-agent-embed"));

    /**
     * 初始化传输器
     */
    public static void installTransPoint() {
        if(null != TransPointStore.transPoint || !UNIFORM_OPEN) {
            return;
        }

        String mq = getStringValue(TRANS_SERVER_PROTOCOL, "MQ");
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
    public void publish(String type, String message) {
        if (null == transPoint) {
            return;
        }

        if(StringUtils.isNullOrEmpty(APPLICATION_NAME)) {
            return;
        }

        EXECUTOR_SERVICE.execute(() -> {
            transPoint.publish(type, message);
        });
    }

}
