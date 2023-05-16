package com.chua.common.support.lang.expression.listener;

/**
 * 监听
 *
 * @author CH
 */
public interface RefreshListener extends Listener {
    /**
     * 消息通知
     *
     * @param source 源码
     */
    void refresh(String source);
}
