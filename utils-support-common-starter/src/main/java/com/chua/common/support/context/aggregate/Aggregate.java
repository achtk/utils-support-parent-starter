package com.chua.common.support.context.aggregate;

import java.net.URL;

/**
 * 聚合
 *
 * @author CH
 */
public interface Aggregate {
    /**
     * 类加载器
     *
     * @return 类加载器
     */
    ClassLoader getClassLoader();

    /**
     * 优先级
     *
     * @return 优先级
     */
    int order();

    /**
     * 是否存在
     *
     * @param name 类名
     * @return 是否存在
     */
    boolean contains(String name);

    /**
     * 加载类
     *
     * @param name       类名
     * @param targetType 类型
     * @param <T>        类型
     * @return 是否存在
     */
    <T> Class<T> forName(String name, Class<T> targetType);

    /**
     * 加载类
     *
     * @param name       类名
     * @return 是否存在
     */
    default Class<?> forName(String name) {
        return forName(name, Object.class);
    }

    /**
     * 原始文件
     * @return 原始文件
     */
    URL getOriginal();
}
