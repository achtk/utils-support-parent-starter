package com.chua.common.support.lang.arrange;

import java.util.Map;

/**
 * 执行器
 * @author CH
 */
public interface ArrangeExecutor<T> {
    /**
     * 执行器
     * @param args 参数对象
     * @return 结果
     */
    T execute(Map<String, Object> args);
}
