package com.chua.common.support.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * 三元元组
 *
 * @author CH
 */
public interface Table<R, C, V> extends com.google.common.collect.Table<R, C, V> {
    /**
     * 获取值
     *
     * @param columnKey c
     * @return v
     */
    Map<R, V> column(C columnKey);

    /**
     * 获取行对应数据
     *
     * @param rowKey r
     * @return C, V
     */
    Map<C, V> row(R rowKey);

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    boolean isEmpty();

    /**
     * 数量
     *
     * @return 数量
     */
    int size();

    /**
     * 赋值
     *
     * @param rowKey    r
     * @param columnKey c
     * @param value     v
     * @return v
     */
    V put(R rowKey, C columnKey, V value);

    /**
     * 存储
     *
     * @param r        r
     * @param c        c
     * @param function function
     * @return v
     */
    V computeIfAbsent(R r, C c, BiFunction<R, C, V> function);

    /**
     * row
     *
     * @return row
     */
    Set<R> rowKeySet();

    /**
     * column map
     *
     * @return map
     */
    Map<C, Map<R, V>> columns();

    /**
     * row map
     *
     * @return map
     */
    Map<R, Map<C, V>> rows();

    /**
     * 结果
     *
     * @return 结果
     */
    Collection<V> values();

    /**
     * column
     * @return column
     */
    Set<C> columnKeySet();
}
