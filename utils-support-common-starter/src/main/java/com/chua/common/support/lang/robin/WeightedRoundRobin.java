package com.chua.common.support.lang.robin;

import com.chua.common.support.annotations.Spi;

import java.util.ArrayList;
import java.util.List;

/**
 * 加权轮询算法
 *
 * @author CH
 */
@Spi("weight")
public class WeightedRoundRobin implements Robin {
    private final List<Node> nodes = new ArrayList<>();
    /**
     * 权重之和
     */
    private int totalWeight = 0;

    /**
     * 按照当前权重（currentWeight）最大值获取IP
     *
     * @return Node
     */
    public Node selectNode() {
        if (nodes.isEmpty()) {
            return null;
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        }

        Node nodeOfMaxWeight = null;
        synchronized (nodes) {
            // 选出当前权重最大的节点
            Node tempNodeOfMaxWeight = null;
            for (Node node : nodes) {
                if (tempNodeOfMaxWeight == null) {
                    tempNodeOfMaxWeight = node;
                } else {
                    tempNodeOfMaxWeight = tempNodeOfMaxWeight.compareTo(node) > 0 ? tempNodeOfMaxWeight : node;
                }
            }
            // 必须new个新的节点实例来保存信息，否则引用指向同一个堆实例，后面的set操作将会修改节点信息
            nodeOfMaxWeight = new Node(tempNodeOfMaxWeight.getContent(), tempNodeOfMaxWeight.getWeight(), tempNodeOfMaxWeight.getEffectiveWeight(), tempNodeOfMaxWeight.getCurrentWeight());
            // 调整当前权重比：按权重（effectiveWeight）的比例进行调整，确保请求分发合理。
            tempNodeOfMaxWeight.setCurrentWeight(tempNodeOfMaxWeight.getCurrentWeight() - totalWeight);
            nodes.forEach(node -> node.setCurrentWeight(node.getCurrentWeight() + node.getEffectiveWeight()));
        }
        return nodeOfMaxWeight;
    }

    @Override
    public Robin create() {
        return new WeightedRoundRobin();
    }

    @Override
    public synchronized Robin clear() {
        nodes.clear();
        return this;
    }

    @Override
    public Robin addNode(Node node) {
        this.nodes.add(node);
        return this;
    }
}
