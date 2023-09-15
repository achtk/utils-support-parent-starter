package com.chua.proxy.support.event;

/**
 * 事件发布者
 *
 * @author CH
 */
public interface EventPublisher<E extends Event> {

    /**
     * 发布事件
     *
     * @param event 事件
     */
    void publishEvent(E event);

    /**
     * 寄存器侦听器
     *
     * @param listener 听众
     */
    void registerListener(EventListener<E> listener);

}
