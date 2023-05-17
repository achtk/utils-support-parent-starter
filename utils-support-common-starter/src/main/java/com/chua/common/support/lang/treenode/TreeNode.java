package com.chua.common.support.lang.treenode;

import com.chua.common.support.utils.StringUtils;
import lombok.Data;

import java.util.*;
import java.util.function.Function;

/**
 * 树
 * 顶层PID必须为 0
 *
 * @author CH
 */
@Data
public class TreeNode<T> {

    /**
     * id
     */
    private String id;
    /**
     *
     */
    private Integer level;
    /**
     * 值
     */
    private String value;
    /**
     * 级联
     */
    private String cascade;
    /**
     * 额为信息
     */
    private T ext;
    /**
     * pid
     */
    private String pid;
    /**
     * 符号
     */
    private String symbol = "-";

    /**
     * 字节点
     */
    private List<TreeNode<T>> children;

    static final TreeNode EMPTY = new TreeNode<>();

    public static <T>TreeNode<T> empty() {
        return EMPTY;
    }

    public void setValue(String value) {
        this.value = value;
        this.cascade = value;
    }

    /**
     * 数据转化
     *
     * @param data     数据
     * @param function 回调
     * @param <T>      类型
     * @return 结果
     */
    public static <T> List<TreeNode<T>> convertFrom(List<T> data, Function<T, TreeNode> function) {
        if (null == data) {
            return Collections.emptyList();
        }

        List<TreeNode<T>> list = new LinkedList<>();
        for (T datum : data) {
            list.add(function.apply(datum));
        }

        return list;
    }

    /**
     * 数据转化
     *
     * @param treeNodes 节点数据
     */
    public static <T> TreeNode<T> transfer(List<TreeNode<T>> treeNodes) {
        Map<String, List<TreeNode<T>>> tpl = new LinkedHashMap<>();
        for (TreeNode<T> treeNode : treeNodes) {
            tpl.computeIfAbsent(treeNode.getPid(), it -> new LinkedList<>()).add(treeNode);
        }

        for (TreeNode<T> treeNode : treeNodes) {
            treeNode.setChildren(tpl.get(treeNode.getId()));
        }

        TreeNode<T> treeNode = new TreeNode<>();
        treeNode.setValue("");
        treeNode.setPid("-1");
        treeNode.setId("0");
        treeNode.setLevel(0);
        treeNode.setCascade(treeNode.getValue());
        treeNode.setChildren(tpl.get(treeNode.getId()));
        doAnalysis(treeNode, 0);

        return treeNode;
    }

    /**
     * 分析层级
     *
     * @param pTreeNode 树
     * @param index     索引
     * @param <T>       类型
     */
    private static <T> void doAnalysis(TreeNode<T> pTreeNode, int index) {
        List<TreeNode<T>> children = pTreeNode.getChildren();
        if (null == children) {
            return;
        }

        for (TreeNode<T> child : children) {
            child.setLevel(index + 1);
            child.setCascade((StringUtils.isNullOrEmpty(pTreeNode.getCascade()) ? "" : pTreeNode.getCascade() + pTreeNode.getSymbol()) + child.getValue());
            doAnalysis(child, index + 1);
        }
    }

    /**
     * 查询根节点
     *
     * @param tpl 数据
     * @param <T> 类型
     * @return 根节点
     */
    private static <T> List<TreeNode<T>> findRoot(Map<String, List<TreeNode<T>>> tpl) {
        List<TreeNode<T>> rs = new LinkedList<>();
        for (List<TreeNode<T>> value : tpl.values()) {
            if (null == value || value.isEmpty()) {
                rs.addAll(value);
            }
        }

        return null;
    }

    /**
     * 數據
     *
     * @return 結果
     */
    public Set<TreeNode<T>> toNodeSet() {
        Set<TreeNode<T>> rs = new HashSet<>();
        List<TreeNode<T>> children = this.children;
        if (null == children) {
            return rs;
        }

        doAnalysisNode(rs, children);
        return rs;
    }

    /**
     * 數據
     *
     * @return 結果
     */
    public List<TreeNode<T>> toNodeList() {
        return new ArrayList<>(toNodeSet());
    }

    /**
     * 數據
     *
     * @return 結果
     */
    public Set<T> toList() {
        Set<T> rs = new HashSet<>();
        List<TreeNode<T>> children = this.children;
        if (null == children) {
            return rs;
        }

        doAnalysis(rs, children);
        return rs;
    }

    /**
     * 分析子节点
     *
     * @param rs       结果
     * @param children 子节点
     */
    private void doAnalysisNode(Set<TreeNode<T>> rs, List<TreeNode<T>> children) {
        if (null == children) {
            return;
        }

        for (TreeNode<T> child : children) {
            doAnalysisNode(rs, child);
        }
    }

    /**
     * 分析子节点
     *
     * @param rs       结果
     * @param children 子节点
     */
    private void doAnalysis(Set<T> rs, List<TreeNode<T>> children) {
        if (null == children) {
            return;
        }

        for (TreeNode<T> child : children) {
            doAnalysis(rs, child);
        }
    }

    /**
     * 分析子节点
     *
     * @param rs    结果
     * @param child 子节点
     */
    private void doAnalysis(Set<T> rs, TreeNode<T> child) {
        rs.add(child.getExt());
        doAnalysis(rs, child.getChildren());
    }

    /**
     * 分析子节点
     *
     * @param rs    结果
     * @param child 子节点
     */
    private void doAnalysisNode(Set<TreeNode<T>> rs, TreeNode<T> child) {
        rs.add(child);
        doAnalysisNode(rs, child.getChildren());
    }
}
