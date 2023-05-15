package com.chua.common.support.lang.watchdog;

import java.util.function.Consumer;

/**
 * 超时观察者
 *
 * @author CH
 */
public interface TimeoutObserver {

    /**
     * Called when the watchdog times out.
     *
     * @param w the watchdog that timed out.
     */
    void timeoutOccured(Watchdog w);

    /**
     * 预处理
     */
    void setProcessNotStarted();

    /**
     * 超时处理
     *
     * @param timeoutHandler 超时处理
     */
    void handler(TimeoutHandler timeoutHandler);

    /**
     * 关闭消费
     *
     * @param closeConsumer 关闭消费
     */
    <T> void observer(Consumer<T> closeConsumer);

    /**
     * 检测异常
     *
     * @throws Exception 异常
     */
    void checkException() throws Exception;

    /**
     * 启动
     *
     * @param process 对象
     */
    void monitor(Object process);

    /**
     * 关闭
     *
     * @param consumer 监听
     */
    void stop(Consumer<Object> consumer);

    /**
     * 关闭
     */
    default void stop() {
        stop(null);
    }
}
