package com.chua.common.support.eventbus;

import java.util.concurrent.Executor;

/**
 * 时间总线
 *
 * @author CH
 */
public interface Eventbus extends AutoCloseable {
    /**
     * 执行器
     *
     * @param executor 执行器
     * @return this
     */
    Eventbus executor(Executor executor);

    /**
     * 注册
     *
     * @param eventbusEvent event
     * @return this
     */
    Eventbus register(EventbusEvent... eventbusEvent);

    /**
     * 注销
     *
     * @param eventbusEvent event
     * @return this
     */
    Eventbus unregister(EventbusEvent eventbusEvent);

    /**
     * 下发消息
     *
     * @param name    名称
     * @param message 消息
     * @return 结果
     */
    Eventbus post(String name, Object message);

    /**
     * 类型
     * @return 类型
     */
    EventbusType event();
}
