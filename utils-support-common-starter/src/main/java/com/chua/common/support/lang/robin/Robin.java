package com.chua.common.support.lang.robin;

import java.util.Collection;

/**
 * 均衡
 *
 * @author CH
 */
public interface Robin {
    /**
     * 选择节点
     *
     * @return 节点
     */
    Node selectNode();

    /**
     * 创建
     *
     * @return 创建
     */
    Robin create();

    /**
     * 清除
     *
     * @return 清除
     */
    Robin clear();

    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点
     */
    Robin addNode(Node node);

    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点
     */
    default Robin addNode(Object... node) {
        for (Object node1 : node) {
            addNode(new Node(node1));
        }
        return this;
    }
    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点
     */
    default Robin addNode(Collection<?> node) {
        for (Object node1 : node) {
            addNode(new Node(node1));
        }
        return this;
    }
    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点
     */
    default Robin addNodes(Node... node) {
        for (Node node1 : node) {
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
    default Robin addNodes(Collection<Node> node) {
        for (Node node1 : node) {
            addNode(node1);
        }
        return this;
    }
}
