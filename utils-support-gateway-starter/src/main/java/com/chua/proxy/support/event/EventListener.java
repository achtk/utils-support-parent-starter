package com.chua.proxy.support.event;

/**
 * rgw事件侦听器
 *
 * @author CH
 */
public interface EventListener<E extends Event> extends java.util.EventListener {

    /**
     * 在事件中
     *
     * @param event 事件
     */
    void onEvent(E event);

}
