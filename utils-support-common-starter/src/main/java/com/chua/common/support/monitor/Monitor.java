package com.chua.common.support.monitor;

/**
 * 监听
 *
 * @author CH
 */
public interface Monitor {
    /**
     * 开启监听
     */
    void start();

    /**
     * 监听间隔(s)
     *
     * @param configuration 监听间隔(s)
     * @return this
     */
    Monitor configuration(MonitorConfiguration configuration);

    /**
     * 监听
     *
     * @param listener 监听
     * @return this
     */
    Monitor addListener(Listener<? extends NotifyMessage> listener);

    /**
     * 关闭监听
     */
    void stop();
}
