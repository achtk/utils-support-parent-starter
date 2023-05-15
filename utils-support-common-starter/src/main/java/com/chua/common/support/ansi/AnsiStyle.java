package com.chua.common.support.ansi;


/**
 * {@link AnsiElement Ansi} styles.
 *
 * @author Phillip Webb
 * @since 1.3.0
 */
public enum AnsiStyle implements AnsiElement {
    /**
     * 0
     */
    NORMAL(0),
    /**
     * 1
     */
    BOLD(1),
    /**
     * 2
     */
    FAINT(2),
    /**
     * 3
     */
    ITALIC(3),
    /**
     * 4
     */
    UNDERLINE(4);

    private final int code;

    AnsiStyle(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code + "";
    }

    @Override
    public int value() {
        return code;
    }

}
