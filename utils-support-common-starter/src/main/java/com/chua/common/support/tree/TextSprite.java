package com.chua.common.support.tree;

import java.util.Objects;

/**
 * This class implements the textual sprite used for printing the binary trees.
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jul 6, 2017)
 */
public final class TextSprite {

    /**
     * The minimum width of a sprite in characters.
     */
    private static final int MINIMUM_SPRITE_WIDTH = 1;

    /**
     * The minimum height of a sprite in characters.
     */
    private static final int MINIMUM_SPRITE_HEIGHT = 1;

    /**
     * The width of this text sprite.
     */
    private final int width;

    /**
     * The height of this text sprite.
     */
    private final int height;

    /**
     * Contains all the characters.
     */
    private final char[][] window;

    /**
     * Constructs a new empty text sprite.
     *
     * @param spriteWidth  the number of text columns.
     * @param spriteHeight the number of text rows.
     */
    public TextSprite(int spriteWidth, int spriteHeight) {
        this.width = checkWidth(spriteWidth);
        this.height = checkHeight(spriteHeight);
        this.window = new char[spriteHeight][spriteWidth];
    }

    /**
     * Sets a particular cell to {@code c}.
     *
     * @param x the x-coordinate of the cell.
     * @param y the y-coordinate of the cell.
     * @param c the character to set.
     */
    public void setChar(int x, int y, char c) {
        checkX(x);
        checkY(y);
        window[y][x] = c;
    }

    /**
     * Reads the content of a particular cell.
     *
     * @param x the x-coordinate of the cell.
     * @param y the y-coordinate of the cell.
     * @return the contents of the cell.
     */
    public char getChar(int x, int y) {
        checkX(x);
        checkY(y);
        return window[y][x];
    }

    /**
     * Returns the number of columns in this sprite.
     *
     * @return the number of columns.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of rows in this sprite.
     *
     * @return the number of rows.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Applies the input text sprite on top of a rectangle of this text sprite.
     *
     * @param textSprite the text sprite to apply.
     * @param xOffset    the horizontal offset from the left border.
     * @param yOffset    the vertical offset from the top border.
     */
    public void apply(TextSprite textSprite, int xOffset, int yOffset) {
        Objects.requireNonNull(textSprite, "The input TextSprite is null!");

        if (xOffset < 0) {
            throw new IndexOutOfBoundsException("xOffset (" + xOffset + ") " +
                    "may not be negative!");
        }

        if (yOffset < 0) {
            throw new IndexOutOfBoundsException("yOffset (" + yOffset + ") " +
                    "may not be negative!");
        }

        if (xOffset + textSprite.getWidth() > getWidth()) {
            throw new IndexOutOfBoundsException("xOffset (" + xOffset + ") " +
                    "is too large! Must be at most " +
                    (getWidth() - textSprite.getWidth()) + ".");
        }

        if (yOffset + textSprite.getHeight() > getHeight()) {
            throw new IndexOutOfBoundsException("yOffset (" + yOffset + ") " +
                    "is too large! Must be at most " +
                    (getHeight() - textSprite.getHeight()) + ".");
        }

        for (int y = 0; y < textSprite.getHeight(); ++y) {
            for (int x = 0; x < textSprite.getWidth(); ++x) {
                setChar(xOffset + x, yOffset + y, textSprite.getChar(x, y));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder((width + 1) * height - 1);
        String separator = "";

        for (int y = 0; y < height; ++y) {
            sb.append(separator);
            separator = "\n";

            for (int x = 0; x < width; ++x) {
                sb.append(getChar(x, y));
            }
        }

        return sb.toString();
    }

    private int checkWidth(int width) {
        if (width < MINIMUM_SPRITE_WIDTH) {
            throw new IllegalArgumentException(
                    "The sprite width is too small (" + width + "). " +
                            "Must be at least " + MINIMUM_SPRITE_WIDTH + ".");
        }

        return width;
    }

    private int checkHeight(int height) {
        if (height < MINIMUM_SPRITE_HEIGHT) {
            throw new IllegalArgumentException(
                    "The sprite height is too small (" + height + "). " +
                            "Must be at least " + MINIMUM_SPRITE_HEIGHT + ".");
        }

        return height;
    }

    private void checkX(int x) {
        if (x < 0) {
            throw new IndexOutOfBoundsException("x = " + x + " is negative.");
        } else if (x >= width) {
            throw new IndexOutOfBoundsException("x = " + x + " exceeds the " +
                    "width = " + width);
        }
    }

    private void checkY(int y) {
        if (y < 0) {
            throw new IndexOutOfBoundsException("y = " + y + " is negative.");
        } else if (y >= height) {
            throw new IndexOutOfBoundsException("y = " + y + " exceeds the " +
                    "height = " + height);
        }
    }
}
