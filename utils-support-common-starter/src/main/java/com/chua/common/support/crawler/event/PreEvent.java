package com.chua.common.support.crawler.event;

/**
 * 事件
 * @author CH
 */
public interface PreEvent<I, O> {
    /**
     * 过滤
     * @param input 输入
     * @return 输出
     */
    O filter(I input);
}
