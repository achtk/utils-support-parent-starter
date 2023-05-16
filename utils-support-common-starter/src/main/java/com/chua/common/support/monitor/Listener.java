package com.chua.common.support.monitor;

/**
 * 监听
 *
 * @author CH
 */
public interface Listener<R extends NotifyMessage> {

    /**
     * 通知
     *
     * @param message 通知
     */
    void onEvent(R message);
}
