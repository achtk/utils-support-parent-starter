package com.chua.common.support.lang.watchdog;


import com.chua.common.support.annotations.Spi;

import java.util.function.Consumer;

/**
 * watchdog
 *
 * @author CH
 */
@Spi("simple")
public interface Watchdog {
    /**
     * 超时时间
     *
     * @param timeout 超时时间
     * @return this
     */
    Watchdog timeout(long timeout);

    /**
     * 添加观察者
     *
     * @param to 观察者
     * @return this
     */
    Watchdog addTimeoutObserver(final TimeoutObserver to);

    /**
     * 删除观察者
     *
     * @param to 观察者
     * @return this
     */
    Watchdog removeTimeoutObserver(final TimeoutObserver to);

    /**
     * 执行
     */
    void fireTimeoutOccured();

    /**
     * 启动
     *
     * @param consumer 监听
     */
    void start(Consumer<Object> consumer);

    /**
     * 关闭
     */
    void stop();
}
