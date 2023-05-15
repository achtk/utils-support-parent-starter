package com.chua.common.support.resource;

import com.chua.common.support.resource.resource.Resource;

import java.util.Set;

/**
 * 资源
 *
 * @author CH
 */
public interface Store {
    /**
     * 获取资源
     *
     * @return 资源
     */
    Set<Resource> getResource();

    /**
     * 第一个
     *
     * @return 第一个
     */
    Resource getFirst();

    /**
     * 获取{type}所有类型
     *
     * @param type 类型
     * @return 获取所有类型
     */
    Set<Class<?>> getTypes(Class<?> type);
}
