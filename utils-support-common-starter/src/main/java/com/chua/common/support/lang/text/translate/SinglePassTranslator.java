package com.chua.common.support.lang.text.translate;


import java.io.IOException;
import java.io.Writer;

/**
 * Abstract translator for processing whole input in single pass.
 * Handles initial index checking and counting of returned code points.
 */
abstract class SinglePassTranslator extends CharSequenceTranslator {

    /**
     * A utility method to be used in the {@link #translate(CharSequence, int, Writer)} method.
     *
     * @return The name of this or the extending class.
     */
    private String getClassName() {
        final Class<? extends SinglePassTranslator> clazz = this.getClass();
        return clazz.isAnonymousClass() ? clazz.getName() : clazz.getSimpleName();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if {@code index != 0}
     */
    @Override
    public int translate(final CharSequence input, final int index, final Writer writer) throws IOException {
        if (index != 0) {
            throw new IllegalArgumentException(getClassName() + ".translate(final CharSequence input, final int "
                    + "index, final Writer out) can not handle a non-zero index.");
        }

        translateWhole(input, writer);

        return Character.codePointCount(input, index, input.length());
    }

    /**
     * Translates whole set of code points passed in input.
     *
     * @param input  CharSequence that is being translated
     * @param writer Writer to translate the text to
     * @throws IOException if and only if the Writer produces an IOException
     */
    abstract void translateWhole(CharSequence input, Writer writer) throws IOException;
}
