package com.chua.common.support.function;

/**
 * 链式
 *
 * @author CH
 */
public interface ChainMap<T, K, V> {

    /**
     * 添加数据
     *
     * @param k k
     * @param v v
     * @return 添加数据
     */
    T add(K k, V v);
}
