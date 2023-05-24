package com.chua.common.support.tree;

/**
 * This interface defines the API for binary tree node printers.
 *
 * @param <T> the type of the binary tree node values.
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jul 6, 2017)
 */
public interface BinaryTreeNodePrinter<T> {

    /**
     * Returns the text sprite representing only the input node.
     *
     * @param node the node to convert into a text sprite.
     * @return the text sprite.
     */
    public TextSprite print(BinaryTreeNode<T> node);
}
