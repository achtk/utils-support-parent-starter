package com.chua.common.support.collection;

import java.util.Iterator;

/**
 * 双向链表
 *
 * @author CH
 */
public interface DoubleLinkedList<E> {
    /**
     * 添加元素
     *
     * @param e 元素
     */
    void addFirst(E e);

    /**
     * 添加元素
     *
     * @param e 元素
     */
    default void add(E e) {
        addLast(e);
    }

    /**
     * 遍历
     *
     * @return 遍历
     */
    Iterator<Node<E>> iteratorNode();

    /**
     * 遍历
     *
     * @return 遍历
     */
    Iterator<E> iterator();

    /**
     * 添加元素
     *
     * @param e 元素
     */
    void addLast(E e);

    /**
     * 查找是否包含关键字key是否在单链表当中
     *
     * @param e 元素
     * @return 查找是否包含关键字key是否在单链表当中
     */
    boolean contains(E e);

    /**
     * 链表长度
     *
     * @return 长度
     */
    int size();

    /**
     * 任意位置插入,第一个数据节点为0号下标
     *
     * @param index 位置
     * @param e     元素
     */
    void add(int index, E e);

    /**
     * 删除
     *
     * @param index 位置
     */
    void remove(int index);

    /**
     * 删除所有值为key的节点
     *
     * @param e 元素
     */
    void removeAllKey(E e);

    /**
     * 清空链表
     */
    void clear();

    /**
     * 第一个元素
     */
    E first();

    /**
     * 最后一个元素
     * @return 最后一个元素
     */
    E last();
}
