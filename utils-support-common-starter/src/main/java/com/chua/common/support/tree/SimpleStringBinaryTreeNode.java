package com.chua.common.support.tree;

/**
 * 字符串
 *
 * @author CH
 */
public class SimpleStringBinaryTreeNode implements BinaryTreeNode<String> {

    public String value;
    public SimpleStringBinaryTreeNode leftChild;
    public SimpleStringBinaryTreeNode rightChild;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public BinaryTreeNode<String> getLeftChild() {
        return leftChild;
    }

    @Override
    public BinaryTreeNode<String> getRightChild() {
        return rightChild;
    }
}