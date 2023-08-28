package com.chua.common.support.lang.text.translate;


import com.chua.common.support.utils.ArrayUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

/**
 * Translates XML numeric entities of the form &amp;#[xX]?\d+;? to
 * the specific code point.
 * <p>
 * Note that the semicolon is optional.
 * @author Administrator
 * @since 1.0
 */
public class NumericEntityUnescaper extends AbstractCharSequenceTranslator {

    /**
     * Enumerates NumericEntityUnescaper options for unescaping.
     */
    public enum OPTION {

        /**
         * Requires a semicolon.
         */
        semiColonRequired,

        /**
         * Does not require a semicolon.
         */
        semiColonOptional,

        /**
         * Throws an exception if a semicolon is missing.
         */
        errorIfNoSemiColon
    }

    /**
     * Default options.
     */
    private static final EnumSet<OPTION> DEFAULT_OPTIONS = EnumSet
            .copyOf(Collections.singletonList(OPTION.semiColonRequired));

    /**
     * EnumSet of OPTIONS, given from the constructor, read-only.
     */
    private final EnumSet<OPTION> options;

    /**
     * Creates a UnicodeUnescaper.
     * <p>
     * The constructor takes a list of options, only one type of which is currently
     * available (whether to allow, error or ignore the semicolon on the end of a
     * numeric entity to being missing).
     * <p>
     * For example, to support numeric entities without a ';':
     * new NumericEntityUnescaper(NumericEntityUnescaper.OPTION.semiColonOptional)
     * and to throw an IllegalArgumentException when they're missing:
     * new NumericEntityUnescaper(NumericEntityUnescaper.OPTION.errorIfNoSemiColon)
     * <p>
     * Note that the default behavior is to ignore them.
     *
     * @param options to apply to this unescaper
     */
    public NumericEntityUnescaper(final OPTION... options) {
        this.options = ArrayUtils.isEmpty(options) ? DEFAULT_OPTIONS : EnumSet.copyOf(Arrays.asList(options));
    }

    /**
     * Tests whether the passed in option is currently set.
     *
     * @param option to check state of
     * @return whether the option is set
     */
    public boolean isSet(final OPTION option) {
        return options.contains(option);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int translate(final CharSequence input, final int index, final Writer writer) throws IOException {
        final int seqEnd = input.length();
        // Uses -2 to ensure there is something after the &#
        if (input.charAt(index) == '&' && index < seqEnd - 2 && input.charAt(index + 1) == '#') {
            int start = index + 2;
            boolean isHex = false;

            final char firstChar = input.charAt(start);
            if (firstChar == 'x' || firstChar == 'X') {
                start++;
                isHex = true;

                // Check there's more than just an x after the &#
                if (start == seqEnd) {
                    return 0;
                }
            }

            int end = start;
            // Note that this supports character codes without a ; on the end
            while (end < seqEnd && (input.charAt(end) >= '0' && input.charAt(end) <= '9'
                    || input.charAt(end) >= 'a' && input.charAt(end) <= 'f'
                    || input.charAt(end) >= 'A' && input.charAt(end) <= 'F')) {
                end++;
            }

            final boolean semiNext = end != seqEnd && input.charAt(end) == ';';

            if (!semiNext) {
                if (isSet(OPTION.semiColonRequired)) {
                    return 0;
                }
                if (isSet(OPTION.errorIfNoSemiColon)) {
                    throw new IllegalArgumentException("Semi-colon required at end of numeric entity");
                }
            }

            final int entityValue;
            try {
                if (isHex) {
                    entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 16);
                } else {
                    entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 10);
                }
            } catch (final NumberFormatException nfe) {
                return 0;
            }

            if (entityValue > 0xFFFF) {
                final char[] chrs = Character.toChars(entityValue);
                writer.write(chrs[0]);
                writer.write(chrs[1]);
            } else {
                writer.write(entityValue);
            }

            return 2 + end - start + (isHex ? 1 : 0) + (semiNext ? 1 : 0);
        }
        return 0;
    }
}
