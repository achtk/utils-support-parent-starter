package com.chua.common.support.collection;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * 三元元组
 *
 * @author CH
 */
public interface Table<R, C, V> {
    /**
     * 是否包含行,列
     *
     * @param rowKey    r
     * @param columnKey c
     * @return 是否包含行, 列
     */
    boolean contains(R rowKey, C columnKey);

    /**
     * 是否包含行
     *
     * @param rowKey r
     * @return 是否包含行
     */
    boolean containsRow(R rowKey);

    /**
     * 是否包含列
     *
     * @param columnKey c
     * @return 是否包含行
     */
    boolean containsColumn(C columnKey);

    /**
     * 是否包含值
     *
     * @param value v
     * @return 是否包含行
     */
    boolean containsValue(V value);

    /**
     * 获取值
     *
     * @param rowKey    r
     * @param columnKey c
     * @return v
     */
    V get(R rowKey, C columnKey);

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
     * 删除
     *
     * @param rowKey    r
     * @param columnKey c
     * @return v
     */
    V remove(R rowKey, C columnKey);

    /**
     * 获取行对应数据
     *
     * @param rowKey r
     * @return C, V
     */
    Map<C, V> row(R rowKey);

    /**
     * 获取列对应数据
     *
     * @param columnKey c
     * @return R, V
     */
    Map<R, V> column(C columnKey);

    /**
     * 存储
     *
     * @param r        r
     * @param c        c
     * @param function function
     * @return v
     */
    V computeIfAbsent(R r, C c, BiFunction<R, C, V> function);
}
