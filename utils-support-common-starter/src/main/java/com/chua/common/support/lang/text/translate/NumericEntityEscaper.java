package com.chua.common.support.lang.text.translate;


import com.chua.common.support.range.Range;

import java.io.IOException;
import java.io.Writer;

/**
 * Translates code points to their XML numeric entity escaped value.
 *
 * @since 1.0
 */
public class NumericEntityEscaper extends CodePointTranslator {

    /**
     * Constructs a {@code NumericEntityEscaper} above the specified value (exclusive).
     *
     * @param codePoint above which to escape
     * @return The newly created {@code NumericEntityEscaper} instance
     */
    public static NumericEntityEscaper above(final int codePoint) {
        return outsideOf(0, codePoint);
    }

    /**
     * Constructs a {@code NumericEntityEscaper} below the specified value (exclusive).
     *
     * @param codePoint below which to escape
     * @return The newly created {@code NumericEntityEscaper} instance
     */
    public static NumericEntityEscaper below(final int codePoint) {
        return outsideOf(codePoint, Integer.MAX_VALUE);
    }

    /**
     * Constructs a {@code NumericEntityEscaper} between the specified values (inclusive).
     *
     * @param codePointLow  above which to escape
     * @param codePointHigh below which to escape
     * @return The newly created {@code NumericEntityEscaper} instance
     */
    public static NumericEntityEscaper between(final int codePointLow, final int codePointHigh) {
        return new NumericEntityEscaper(codePointLow, codePointHigh, true);
    }

    /**
     * Constructs a {@code NumericEntityEscaper} outside of the specified values (exclusive).
     *
     * @param codePointLow  below which to escape
     * @param codePointHigh above which to escape
     * @return The newly created {@code NumericEntityEscaper} instance
     */
    public static NumericEntityEscaper outsideOf(final int codePointLow, final int codePointHigh) {
        return new NumericEntityEscaper(codePointLow, codePointHigh, false);
    }

    /**
     * whether to escape between the boundaries or outside them.
     */
    private final boolean between;

    /**
     * range from lowest code point to highest code point.
     */
    private final Range<Integer> range;

    /**
     * Constructs a {@code NumericEntityEscaper} for all characters.
     */
    public NumericEntityEscaper() {
        this(0, Integer.MAX_VALUE, true);
    }

    /**
     * Constructs a {@code NumericEntityEscaper} for the specified range. This is
     * the underlying method for the other constructors/builders. The {@code below}
     * and {@code above} boundaries are inclusive when {@code between} is
     * {@code true} and exclusive when it is {@code false}.
     *
     * @param below   int value representing the lowest code point boundary
     * @param above   int value representing the highest code point boundary
     * @param between whether to escape between the boundaries or outside them
     */
    private NumericEntityEscaper(final int below, final int above, final boolean between) {
        this.range = Range.closed(below, above);
        this.between = between;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean translate(final int codePoint, final Writer writer) throws IOException {
        if (this.between != this.range.contains(codePoint)) {
            return false;
        }
        writer.write("&#");
        writer.write(Integer.toString(codePoint, 10));
        writer.write(';');
        return true;
    }
}
