package com.chua.common.support.lang.function;

/**
 * 引擎
 * @author CH
 */
public interface Engine {
    /**
     * 获取实现
     * @param target 目标类型
     * @param <T> 类型
     */
    <T>T get(Class<T> target);
}
