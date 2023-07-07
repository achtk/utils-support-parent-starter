package com.chua.common.support.lang.robin;

import java.util.Collection;

/**
 * 均衡
 *
 * @author CH
 */
public interface Robin<T> {
    /**
     * 选择节点
     *
     * @return 节点
     */
    Node<T> selectNode();

    /**
     * 创建
     *
     * @return 创建
     */
    Robin<T> create();

    /**
     * 清除
     *
     * @return 清除
     */
    Robin<T> clear();

    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点
     */
    Robin<T> addNode(Node<T> node);

    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点
     */
    default Robin<T> addNode(T... node) {
        for (T node1 : node) {
            addNode(new Node<>(node1));
        }
        return this;
    }
    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点
     */
    default Robin<T> addNode(Collection<T> node) {
        for (T node1 : node) {
            addNode(new Node<>(node1));
        }
        return this;
    }
    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点
     */
    default Robin<T> addNodes(Node<T>... node) {
        for (Node<T> node1 : node) {
            addNode(node1);
        }
        return this;
    }

    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点
     */
    default Robin<T> addNodes(Collection<Node<T>> node) {
        for (Node<T> node1 : node) {
            addNode(node1);
        }
        return this;
    }
}
