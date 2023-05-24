package com.chua.common.support.tree.support;

import com.chua.common.support.tree.BinaryTreeNode;
import com.chua.common.support.tree.BinaryTreeNodePrinter;
import com.chua.common.support.tree.BinaryTreePrinter;
import com.chua.common.support.tree.TextSprite;

import static com.chua.common.support.constant.NumberConstant.SECOND;

/**
 * Implements a default binary tree printer.
 *
 * @param <T> the type of the data contained in the binary tree nodes.
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jul 6, 2017)
 */
public final class DefaultBinaryTreePrinter<T> implements BinaryTreePrinter<T> {

    /**
     * When combining the text sprites of two sibling subtrees, by default, at
     * least one character worth horizontal space will be put between the two
     * sprites.
     */
    private static final int DEFAULT_MINIMUM_SIBLING_SPACE = 1;

    /**
     * The default character for printing arrow tips.
     */
    private static final char DEFAULT_ARROW_TIP_CHARACTER = 'V';

    /**
     * The minimum number of spaces between two siblings.
     */
    private int siblingSpace = DEFAULT_MINIMUM_SIBLING_SPACE;

    /**
     * The arrow tip character.
     */
    private char arrowTipCharacter = DEFAULT_ARROW_TIP_CHARACTER;

    @Override
    public String print(BinaryTreeNode<T> root,
                        BinaryTreeNodePrinter<T> nodePrinter) {
        if (root == null) {
            return "null";
        }

        TextSprite textSprite = printImpl(root, nodePrinter).textSprite;
        Utils.setEmptyTextSpriteCellsToSpace(textSprite);
        return textSprite.toString();
    }

    public int getSiblingSpace() {
        return siblingSpace;
    }

    public void setSiblingSpace(int siblingSpace) {
        this.siblingSpace = checkSiblingSpace(siblingSpace);
    }

    public char getArrowTipCharacter() {
        return arrowTipCharacter;
    }

    public void setArrowTipCharacter(char arrowTipCharacter) {
        this.arrowTipCharacter = arrowTipCharacter;
    }

    private SubtreeDescriptor printImpl(BinaryTreeNode<T> node,
                                        BinaryTreeNodePrinter<T> nodePrinter) {
        if (node.getLeftChild() == null && node.getRightChild() == null) {
            TextSprite leafNodeTextSprite = nodePrinter.print(node);
            SubtreeDescriptor subtreeDescriptor = new SubtreeDescriptor();
            subtreeDescriptor.rootNodeOffset = 0;
            subtreeDescriptor.rootNodeWidth = leafNodeTextSprite.getWidth();
            subtreeDescriptor.textSprite = leafNodeTextSprite;
            return subtreeDescriptor;
        }

        if (node.getLeftChild() != null && node.getRightChild() != null) {
            return printWithTwoChildrenImpl(node, nodePrinter);
        }

        if (node.getLeftChild() != null) {
            return printWithLeftChildImpl(node, nodePrinter);
        }

        return printWithRightChildImpl(node, nodePrinter);
    }

    private SubtreeDescriptor printWithTwoChildrenImpl(
            BinaryTreeNode<T> node,
            BinaryTreeNodePrinter<T> nodePrinter) {
        SubtreeDescriptor subtreeDescriptor = new SubtreeDescriptor();
        SubtreeDescriptor leftChildDescriptor = printImpl(node.getLeftChild(),
                nodePrinter);

        SubtreeDescriptor rightChildDescriptor = printImpl(node.getRightChild(),
                nodePrinter);

        TextSprite nodeTextSprite = nodePrinter.print(node);
        TextSprite leftChildTextSprite = leftChildDescriptor.textSprite;
        TextSprite rightChildTextSprite = rightChildDescriptor.textSprite;

        // The height of the resulting text sprite.
        int subtreeTextSpriteHeight = 1 + nodeTextSprite.getHeight() +
                Math.max(leftChildTextSprite.getHeight(),
                        rightChildTextSprite.getHeight());

        int aLeft = (nodeTextSprite.getWidth() - siblingSpace) / 2;
        int aRight = nodeTextSprite.getWidth() - siblingSpace - aLeft;

        int bLeft = leftChildTextSprite.getWidth() -
                leftChildDescriptor.rootNodeOffset -
                leftChildDescriptor.rootNodeWidth;

        int leftPartOffset = 0;

        if (aLeft + SECOND > bLeft + leftChildDescriptor.rootNodeWidth) {
            leftPartOffset = aLeft + 2
                    - bLeft
                    - leftChildDescriptor.rootNodeWidth;
        }

        int rightPartOffset = 0;

        if (rightChildDescriptor.rootNodeOffset +
                rightChildDescriptor.rootNodeWidth < aRight + 2) {
            rightPartOffset = aRight + 2 - rightChildDescriptor.rootNodeOffset
                    - rightChildDescriptor.rootNodeWidth;
        }

        // The width of the resulting text sprite.
        int subtreeTextSpriteWidth =
                leftChildTextSprite.getWidth() +
                        leftPartOffset +
                        siblingSpace +
                        rightPartOffset +
                        rightChildTextSprite.getWidth();

        TextSprite subtreeTextSprite = new TextSprite(subtreeTextSpriteWidth,
                subtreeTextSpriteHeight);

        subtreeTextSprite.apply(leftChildTextSprite,
                0,
                nodeTextSprite.getHeight() + 1);

        subtreeTextSprite.apply(rightChildTextSprite,
                leftChildTextSprite.getWidth() +
                        leftPartOffset +
                        siblingSpace +
                        rightPartOffset,
                nodeTextSprite.getHeight() + 1);

        int leftArrowLength = Math.max(1,
                leftChildTextSprite.getWidth() +
                        leftPartOffset
                        - aLeft + 1
                        - leftChildDescriptor.rootNodeOffset
                        - leftChildDescriptor.rootNodeWidth / 2);
        int rightArrowLength = Math.max(1,
                rightPartOffset +
                        rightChildDescriptor.rootNodeOffset +
                        rightChildDescriptor.rootNodeWidth / 2 -
                        aRight);

        int totalArrowLength = leftArrowLength + rightArrowLength;
        int nodeSpriteShift = totalArrowLength / 2 - leftArrowLength;

        subtreeTextSprite.apply(nodeTextSprite,
                nodeSpriteShift +
                        leftChildTextSprite.getWidth() +
                        leftPartOffset - aLeft,
                0);

        rightArrowLength = totalArrowLength / 2;
        leftArrowLength = totalArrowLength - rightArrowLength;

        int arrowStartX = leftChildTextSprite.getWidth() + leftPartOffset
                - aLeft
                + nodeSpriteShift;
        int arrowY = nodeTextSprite.getHeight() - 2;

        for (int x = 0; x < leftArrowLength; ++x) {
            subtreeTextSprite.setChar(arrowStartX - x - 1, arrowY, '-');
        }

        subtreeTextSprite.setChar(arrowStartX - leftArrowLength,
                arrowY,
                '+');

        subtreeTextSprite.setChar(arrowStartX - leftArrowLength,
                arrowY + 1,
                '|');

        subtreeTextSprite.setChar(arrowStartX - leftArrowLength,
                arrowY + 2,
                arrowTipCharacter);

        arrowStartX = leftChildTextSprite.getWidth()
                + leftPartOffset
                - aLeft
                + nodeTextSprite.getWidth()
                + nodeSpriteShift;

        for (int x = 0; x < rightArrowLength; ++x) {
            subtreeTextSprite.setChar(arrowStartX + x, arrowY, '-');
        }

        subtreeTextSprite.setChar(arrowStartX + rightArrowLength, arrowY, '+');
        subtreeTextSprite.setChar(arrowStartX + rightArrowLength,
                arrowY + 1,
                '|');

        subtreeTextSprite.setChar(arrowStartX + rightArrowLength,
                arrowY + 2,
                arrowTipCharacter);

        subtreeDescriptor.rootNodeOffset = leftChildTextSprite.getWidth()
                + leftPartOffset
                - aLeft;

        subtreeDescriptor.rootNodeWidth = nodeTextSprite.getWidth();
        subtreeDescriptor.textSprite = subtreeTextSprite;
        return subtreeDescriptor;
    }

    private SubtreeDescriptor printWithLeftChildImpl(
            BinaryTreeNode<T> node,
            BinaryTreeNodePrinter<T> nodePrinter) {
        SubtreeDescriptor subtreeDescriptor = new SubtreeDescriptor();
        SubtreeDescriptor leftChildDescriptor = printImpl(node.getLeftChild(),
                nodePrinter);

        TextSprite nodeTextSprite = nodePrinter.print(node);
        TextSprite leftChildTextSprite = leftChildDescriptor.textSprite;

        // The height of the resulting text sprite.
        int subtreeTextSpriteHeight = 1 + nodeTextSprite.getHeight()
                + leftChildTextSprite.getHeight();

        int a = (nodeTextSprite.getWidth() - siblingSpace) / 2;

        int b = leftChildDescriptor.textSprite.getWidth()
                - leftChildDescriptor.rootNodeOffset
                - leftChildDescriptor.rootNodeWidth;

        int leftPartOffset = 0;

        if (a + SECOND > b + leftChildDescriptor.rootNodeWidth) {
            leftPartOffset = a + 2 - b - leftChildDescriptor.rootNodeWidth;
        }

        // The width of the resulting text sprite.
        int subtreeTextSpriteWidth =
                leftChildTextSprite.getWidth() +
                        leftPartOffset +
                        nodeTextSprite.getWidth() -
                        a;

        TextSprite subtreeTextSprite = new TextSprite(subtreeTextSpriteWidth,
                subtreeTextSpriteHeight);
        subtreeTextSprite.apply(nodeTextSprite,
                leftChildDescriptor.textSprite.getWidth() +
                        leftPartOffset - a,
                0);

        subtreeTextSprite.apply(leftChildTextSprite,
                0,
                nodeTextSprite.getHeight() + 1);

        int arrowLength = Math.max(1, leftChildTextSprite.getWidth() +
                leftPartOffset
                - a + 1
                - leftChildDescriptor.rootNodeOffset
                - leftChildDescriptor.rootNodeWidth / 2);

        int arrowStartX = leftChildDescriptor.textSprite.getWidth()
                + leftPartOffset - a;

        int arrowY = nodeTextSprite.getHeight() - 2;

        for (int x = 0; x < arrowLength; ++x) {
            subtreeTextSprite.setChar(arrowStartX - x - 1, arrowY, '-');
        }

        subtreeTextSprite.setChar(arrowStartX - arrowLength, arrowY, '+');
        subtreeTextSprite.setChar(arrowStartX - arrowLength, arrowY + 1, '|');
        subtreeTextSprite.setChar(arrowStartX - arrowLength, arrowY + 2, '|');

        subtreeDescriptor.rootNodeOffset = leftChildTextSprite.getWidth()
                + leftPartOffset - a;
        subtreeDescriptor.rootNodeWidth = nodeTextSprite.getWidth();
        subtreeDescriptor.textSprite = subtreeTextSprite;
        return subtreeDescriptor;
    }

    private SubtreeDescriptor printWithRightChildImpl(
            BinaryTreeNode<T> node,
            BinaryTreeNodePrinter<T> nodePrinter) {
        SubtreeDescriptor subtreeDescriptor = new SubtreeDescriptor();
        SubtreeDescriptor rightChildDescriptor = printImpl(node.getRightChild(),
                nodePrinter);

        TextSprite nodeTextSprite = nodePrinter.print(node);
        TextSprite rightChildTextSprite = rightChildDescriptor.textSprite;

        // The height of the resulting text sprite.
        int subtreeTextSpriteHeight = 1 + nodeTextSprite.getHeight()
                + rightChildTextSprite.getHeight();

        // Number of spaces on the right side of the sibling separator.
        int a = (nodeTextSprite.getWidth() - siblingSpace) / 2;

        int rightPartOffset = 0;

        if (rightChildDescriptor.rootNodeOffset +
                rightChildDescriptor.rootNodeWidth < a + 2) {
            rightPartOffset = a + 2 - rightChildDescriptor.rootNodeOffset -
                    rightChildDescriptor.rootNodeWidth;
        }

        // The width of the resulting text sprite.
        int subtreeTextSpriteWidth =
                nodeTextSprite.getWidth()
                        - a
                        + rightPartOffset
                        + rightChildTextSprite.getWidth();

        TextSprite subtreeTextSprite = new TextSprite(subtreeTextSpriteWidth,
                subtreeTextSpriteHeight);
        subtreeTextSprite.apply(nodeTextSprite, 0, 0);
        subtreeTextSprite.apply(rightChildTextSprite,
                nodeTextSprite.getWidth() - a + rightPartOffset,
                1 + nodeTextSprite.getHeight());

        int arrowLength = Math.max(1,
                rightPartOffset +
                        rightChildDescriptor.rootNodeOffset +
                        rightChildDescriptor.rootNodeWidth / 2 - a);

        int arrowStartX = nodeTextSprite.getWidth();
        int arrowY = nodeTextSprite.getHeight() - 2;

        for (int x = 0; x < arrowLength; ++x) {
            subtreeTextSprite.setChar(arrowStartX + x, arrowY, '-');
        }

        subtreeTextSprite.setChar(arrowStartX + arrowLength, arrowY, '+');
        subtreeTextSprite.setChar(arrowStartX + arrowLength, arrowY + 1, '|');
        subtreeTextSprite.setChar(arrowStartX + arrowLength, arrowY + 2, '|');

        subtreeDescriptor.rootNodeOffset = 0;
        subtreeDescriptor.rootNodeWidth = nodeTextSprite.getWidth();
        subtreeDescriptor.textSprite = subtreeTextSprite;
        return subtreeDescriptor;
    }

    private int checkSiblingSpace(int siblingSpace) {
        if (siblingSpace < 0) {
            throw new IllegalArgumentException("Sibling space is negative: " +
                    siblingSpace);
        }

        return siblingSpace;
    }

    private static final class SubtreeDescriptor {

        TextSprite textSprite;
        int rootNodeOffset;
        int rootNodeWidth;
    }
}
