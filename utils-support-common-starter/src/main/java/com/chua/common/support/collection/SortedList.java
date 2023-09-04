package com.chua.common.support.collection;

import java.util.Comparator;
import java.util.List;

/**
 * 排序列表
 * 排序List
 *
 * @author CH
 * @version 1.0.0
 * @date 2023/09/04
 * @since 2021/4/7
 */
@SuppressWarnings("ALL")
public interface SortedList<E> extends List<E> {
    /**
     * 空数据
     *
     * @return 空数据
     */
    static <E> SortedList<E> emptyList() {
        return (SortedList<E>) SortedArrayList.EMPTY;
    }


    /**
     * 转为List
     *
     * @return List
     */
    default List<E> toList() {
        return this;
    }

    /**
     * 比较器
     *
     * @return 比较器
     */
    Comparator<? super E> comparator();

    /**
     * 第一个元素
     *
     * @return 第一个元素
     */
    E first();

    /**
     * 最后一个元素
     *
     * @return 最后一个元素
     */
    E last();
}
