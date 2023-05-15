package com.chua.common.support.ansi;


/**
 * {@link AnsiElement Ansi} colors.
 *
 * @author Phillip Webb
 * @author Geoffrey Chandler
 * @since 1.3.0
 */
public enum AnsiColor implements AnsiElement {
    /**
     * 39
     */
    DEFAULT(39),
    /**
     * 30
     */
    BLACK(30),
    /**
     * 31
     */
    RED(31),
    /**
     * 32
     */
    GREEN(32),
    /**
     * 33
     */
    YELLOW(33),
    /**
     * 34
     */
    BLUE(34),
    /**
     * 35
     */
    MAGENTA(35),
    /**
     * 36
     */
    CYAN(36),
    /**
     * 37
     */
    WHITE(37),
    /**
     * 90
     */
    BRIGHT_BLACK(90),
    /**
     * 91
     */
    BRIGHT_RED(91),
    /**
     * 92
     */
    BRIGHT_GREEN(92),
    /**
     * 93
     */
    BRIGHT_YELLOW(93),
    /**
     * 94
     */
    BRIGHT_BLUE(94),
    /**
     * 95
     */
    BRIGHT_MAGENTA(95),
    /**
     * 96
     */
    BRIGHT_CYAN(96),
    /**
     * 97
     */
    BRIGHT_WHITE(97);

    private final int code;

    AnsiColor(int code) {
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
