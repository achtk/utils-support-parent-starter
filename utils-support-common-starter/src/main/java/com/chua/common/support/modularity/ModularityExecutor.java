package com.chua.common.support.modularity;

import java.util.Map;

/**
 * 执行器
 * @author CH
 */
public interface ModularityExecutor<T> {
    /**
     * 执行器
     * @param args 参数对象
     * @return 结果
     */
    T execute(Map<String, Object> args);
}
