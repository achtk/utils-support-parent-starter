package com.chua.common.support.collection;

import java.util.Collection;
import java.util.Iterator;

/**
 * 链式
 *
 * @author CH
 */
public interface Chain<V, R> {

    /**
     * 添加数据
     *
     * @param v v
     * @return 添加数据
     */
    R addChain(V v);

    /**
     * 添加数据
     *
     * @param v v
     * @return 添加数据
     */
    R addChains(Collection<V> v);

    /**
     * 迭代器
     * @return 迭代器
     */

    Iterator<V> iterator();
}
