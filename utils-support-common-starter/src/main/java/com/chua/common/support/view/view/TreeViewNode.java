package com.chua.common.support.view.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CH
 */
public class TreeViewNode {
    private static final String TIME_UNIT = "ms";

    /**
     * 父节点
     */
    final TreeViewNode parent;

    /**
     * 节点数据
     */
    final String data;

    /**
     * 子节点
     */
    final List<TreeViewNode> children = new ArrayList<TreeViewNode>();

    final Map<String, TreeViewNode> map = new HashMap<String, TreeViewNode>();

    /**
     * 开始时间戳
     */
    private long beginTimestamp;

    /**
     * 结束时间戳
     */
    private long endTimestamp;

    /**
     * 备注
     */
    public String mark;

    /**
     * 构造树节点(根节点)
     */
    protected TreeViewNode(String data) {
        this.parent = null;
        this.data = data;
    }


    public static TreeViewNode newBuilder(String data) {
        return new TreeViewNode(data);
    }

    /**
     * 构造树节点
     *
     * @param parent 父节点
     * @param data   节点数据
     */
    public TreeViewNode(TreeViewNode parent, String data) {
        this.parent = parent;
        this.data = data;
        parent.children.add(this);
        parent.map.put(data, this);
    }

    public TreeViewNode addChildren(TreeViewNode node) {
        this.children.add(node);
        return this;
    }

    /**
     * 查找已经存在的节点
     */
    TreeViewNode find(String data) {
        return map.get(data);
    }

    /**
     * 是否根节点
     *
     * @return true / false
     */
    boolean isRoot() {
        return null == parent;
    }

    /**
     * 是否叶子节点
     *
     * @return true / false
     */
    boolean isLeaf() {
        return children.isEmpty();
    }

    TreeViewNode markBegin() {
        beginTimestamp = System.nanoTime();
        return this;
    }

    TreeViewNode markEnd() {
        endTimestamp = System.nanoTime();

        long cost = getCost();
        if (cost < minCost) {
            minCost = cost;
        }
        if (cost > maxCost) {
            maxCost = cost;
        }
        times++;
        totalCost += cost;

        return this;
    }

    TreeViewNode mark(String mark) {
        this.mark = mark;
        marks++;
        return this;
    }

    long getCost() {
        return endTimestamp - beginTimestamp;
    }

    /**
     * convert nano-seconds to milli-seconds
     */
    double getCostInMillis(long nanoSeconds) {
        return nanoSeconds / 1000000.0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (times <= 1) {
            sb.append("[").append(getCostInMillis(getCost())).append(TIME_UNIT).append("] ");
        } else {
            sb.append("[min=").append(getCostInMillis(minCost)).append(TIME_UNIT).append(",max=")
                    .append(getCostInMillis(maxCost)).append(TIME_UNIT).append(",total=")
                    .append(getCostInMillis(totalCost)).append(TIME_UNIT).append(",count=")
                    .append(times).append("] ");
        }
        return sb.toString();
    }

    /**
     * 合并统计相同调用,并计算最小\最大\总耗时
     */
    private long minCost = Long.MAX_VALUE;
    private long maxCost = Long.MIN_VALUE;
    public long totalCost = 0;
    private long times = 0;
    public long marks = 0;
}
