package com.chua.common.support.tree;

/**
 * This interface describes the API for binary tree nodes.
 *
 * @param <T> the value type.
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jul 6, 2017)
 */
public interface BinaryTreeNode<T> {

    /**
     * Returns the value of this node.
     *
     * @return the value of this node.
     */
    T getValue();

    /**
     * Returns the left child of this node or {@code null} if there is no such.
     *
     * @return the left child.
     */
    BinaryTreeNode<T> getLeftChild();

    /**
     * Returns the right child of this node or {@code null} if there is no such.
     *
     * @return the right child.
     */
    BinaryTreeNode<T> getRightChild();
}
