package com.chua.common.support.tree;

/**
 * int
 *
 * @author CH
 */
public class IntBinaryTreeNode implements BinaryTreeNode<Integer> {

    public Integer value;
    public IntBinaryTreeNode leftChild;
    public IntBinaryTreeNode rightChild;

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public BinaryTreeNode<Integer> getLeftChild() {
        return leftChild;
    }

    @Override
    public BinaryTreeNode<Integer> getRightChild() {
        return rightChild;
    }
}