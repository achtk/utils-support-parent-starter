package com.chua.common.support.task.disruptor;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 分发器
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/30
 */
public interface Disruptor<E> extends AutoCloseable {
    /**
     * 事件
     *
     * @param eventActor 事件
     */
    void handleEventsWith(EventActor<? super E> eventActor);

    /**
     * 初始化
     *
     * @param type          类型
     * @param bufferSize    大小
     * @param threadFactory 线程工厂
     * @param executor      线程池
     * @param entityFactory 实体工厂
     */
    void initial(Class<E> type, int bufferSize, ThreadFactory threadFactory, Executor executor, EntityFactory<E> entityFactory);

    /**
     * 发布消息
     *
     * @param message 消息
     */
    void publish(E message);

    /**
     * 发布消息
     *
     * @param consumer 消费者
     */
    void publish(Consumer<E> consumer);

    /**
     * 开始
     */
    void start();

    /**
     * 关闭
     *
     * @param timeout  超时
     * @param timeUnit 时间
     */
    default void shutdown(long timeout, TimeUnit timeUnit) {
        try {
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭
     */
    default void shutdown() {
        shutdown(-1L, TimeUnit.MILLISECONDS);
    }

    /**
     * 状态
     *
     * @return 状态
     */
    int statue();

    /**
     * 配置
     *
     * @param properties 配置
     */
    default void configuration(Properties properties) {

    }


}
