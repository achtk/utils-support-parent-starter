package com.chua.common.support.mapping;

/**
 * mapping
 *
 * @author CH
 */
public interface MappingResolver {
    /**
     * 初始化对象
     *
     * @param target 目标类型
     * @param <T>    类型
     * @return 对象
     */
    <T> T create(Class<T> target);
}
