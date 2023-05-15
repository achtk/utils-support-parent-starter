package com.chua.common.support.ansi;

/**
 * Provides a fluent API for generating ANSI escape sequences.
 *
 * @author Administrator
 * @since 1.0
 */
public enum AnsiAttribute implements AnsiElement {
    /**
     * reset
     */
    RESET(0),
    /**
     * reset
     */
    INTENSITY_BOLD(1),
    /**
     * INTENSITY_FAINT
     */
    INTENSITY_FAINT(2),
    /**
     * ITALIC
     */
    ITALIC(3),
    /**
     * UNDERLINE
     */
    UNDERLINE(4),
    /**
     * BLINK_SLOW
     */
    BLINK_SLOW(5),
    /**
     * BLINK_FAST
     */
    BLINK_FAST(6),
    /**
     * NEGATIVE_ON
     */
    NEGATIVE_ON(7),
    /**
     * CONCEAL_ON
     */
    CONCEAL_ON(8),
    /**
     * STRIKETHROUGH_ON
     */
    STRIKETHROUGH_ON(9),
    /**
     * UNDERLINE_DOUBLE
     */
    UNDERLINE_DOUBLE(21),
    /**
     * INTENSITY_BOLD_OFF
     */
    INTENSITY_BOLD_OFF(22),
    /**
     * ITALIC_OFF
     */
    ITALIC_OFF(23),
    /**
     * UNDERLINE_OFF
     */
    UNDERLINE_OFF(24),
    /**
     * BLINK_OFF
     */
    BLINK_OFF(25),
    /**
     * NEGATIVE_OFF
     */
    NEGATIVE_OFF(27),
    /**
     * CONCEAL_OFF
     */
    CONCEAL_OFF(28),
    /**
     * STRIKETHROUGH_OFF
     */
    STRIKETHROUGH_OFF(29);

    private final int value;

    AnsiAttribute(int index) {
        this.value = index;
    }

    @Override
    public String toString() {
        return value + "";
    }

    public int value() {
        return value;
    }

}