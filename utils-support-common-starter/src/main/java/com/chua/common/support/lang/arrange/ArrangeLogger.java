package com.chua.common.support.lang.arrange;

/**
 * 日志
 *
 * @author CH
 */
public interface ArrangeLogger {
    /**
     * 监听
     *
     * @param cost    耗时
     * @param message 消息
     * @param name    任务名称
     */
    void listen(String message, String name, long cost);
}
