package com.chua.agent.support.log;

import com.chua.agent.support.span.span.Span;

/**
 * 日志解释器
 * @author CH
 */
public interface LogResolver {
    /***
     * 注册日志
     * @param span 链路
     */
    void register(Span span);
    /***
     * 注册日志
     * @param message 消息
     */
    void register(Object message);
}
