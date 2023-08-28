package com.chua.common.support.view.view;

import com.chua.common.support.ansi.Ansi;
import com.chua.common.support.ansi.AnsiColor;
import com.chua.common.support.utils.StringUtils;

/**
 * 树形控件
 * Created by vlinux on 15/5/26.
 */
public class TreeView implements View {

    private static final String STEP_FIRST_CHAR = "`---";
    private static final String STEP_NORMAL_CHAR = "+---";
    private static final String STEP_HAS_BOARD = "|   ";
    private static final String STEP_EMPTY_BOARD = "    ";

    // 是否输出耗时
    private final boolean isPrintCost;

    // 根节点
    private final TreeViewNode root;

    // 当前节点
    private TreeViewNode current;

    // 最耗时的节点
    private TreeViewNode maxCost;


    public TreeView(boolean isPrintCost, String title) {
        this.root = TreeViewNode.newBuilder(title).markBegin().markEnd();
        this.current = root;
        this.isPrintCost = isPrintCost;
    }

    public TreeView(boolean isPrintCost, TreeViewNode root) {
        this.root = root;
        this.current = root;
        this.isPrintCost = isPrintCost;
    }

    @Override
    public String draw() {

        findMaxCostNode(root);

        final StringBuilder builder = new StringBuilder();

        final Ansi highlighted = Ansi.ansi().fg(AnsiColor.RED);

        recursive(0, true, "", root, new Callback() {

            @Override
            public void callback(int deep, boolean isLast, String prefix, TreeViewNode treeViewNode) {
                builder.append(prefix).append(isLast ? STEP_FIRST_CHAR : STEP_NORMAL_CHAR);
                if (isPrintCost && !treeViewNode.isRoot()) {
                    if (treeViewNode == maxCost) {
                        // the TreeViewNode with max cost will be highlighted
                        builder.append(highlighted.a(treeViewNode.toString()).reset().toString());
                    } else {
                        builder.append(treeViewNode.toString());
                    }
                }
                builder.append(treeViewNode.data);
                if (!StringUtils.isBlank(treeViewNode.mark)) {
                    builder.append(" [").append(treeViewNode.mark).append(treeViewNode.marks > 1 ? "," + treeViewNode.marks : "").append("]");
                }
                builder.append("\n");
            }

        });

        return builder.toString();
    }

    /**
     * 递归遍历
     */
    private void recursive(int deep, boolean isLast, String prefix, TreeViewNode treeViewNode, Callback callback) {
        callback.callback(deep, isLast, prefix, treeViewNode);
        if (!treeViewNode.isLeaf()) {
            final int size = treeViewNode.children.size();
            for (int index = 0; index < size; index++) {
                final boolean isLastFlag = index == size - 1;
                final String currentPrefix = isLast ? prefix + STEP_EMPTY_BOARD : prefix + STEP_HAS_BOARD;
                recursive(
                        deep + 1,
                        isLastFlag,
                        currentPrefix,
                        treeViewNode.children.get(index),
                        callback
                );
            }
        }
    }

    /**
     * 查找耗时最大的节点，便于后续高亮展示
     *
     * @param treeViewNode
     */
    private void findMaxCostNode(TreeViewNode treeViewNode) {
        if (!treeViewNode.isRoot() && !treeViewNode.parent.isRoot()) {
            if (maxCost == null) {
                maxCost = treeViewNode;
            } else if (maxCost.totalCost < treeViewNode.totalCost) {
                maxCost = treeViewNode;
            }
        }
        if (!treeViewNode.isLeaf()) {
            for (TreeViewNode n : treeViewNode.children) {
                findMaxCostNode(n);
            }
        }
    }


    /**
     * 创建一个分支节点
     *
     * @param data 节点数据
     * @return this
     */
    public TreeView begin(String data) {
        TreeViewNode n = current.find(data);
        if (n != null) {
            current = n;
        } else {
            current = new TreeViewNode(current, data);
        }
        current.markBegin();
        return this;
    }

    /**
     * 结束一个分支节点
     *
     * @return this
     */
    public TreeView end() {
        if (current.isRoot()) {
            throw new IllegalStateException("current TreeViewNode is root.");
        }
        current.markEnd();
        current = current.parent;
        return this;
    }

    /**
     * 结束一个分支节点,并带上备注
     *
     * @return this
     */
    public TreeView end(String mark) {
        if (current.isRoot()) {
            throw new IllegalStateException("current TreeViewNode is root.");
        }
        current.markEnd().mark(mark);
        current = current.parent;
        return this;
    }



    /**
     * 遍历回调接口
     */
    private interface Callback {

        void callback(int deep, boolean isLast, String prefix, TreeViewNode treeViewNode);

    }

}
