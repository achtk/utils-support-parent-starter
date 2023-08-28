package com.chua.common.support.lang.text.translate;


import java.io.IOException;
import java.io.Writer;

/**
 * Helper subclass to CharSequenceTranslator to remove unpaired surrogates.
 *
 * @since 1.0
 */
public class UnicodeUnpairedSurrogateRemover extends AbstractCodePointTranslator {
    /**
     * Implementation of translate that throws out unpaired surrogates.
     * {@inheritDoc}
     */
    @Override
    public boolean translate(final int codePoint, final Writer writer) throws IOException {
        // If true, it is a surrogate. Write nothing and say we've translated. Otherwise return false, and don't translate it.
        return codePoint >= Character.MIN_SURROGATE && codePoint <= Character.MAX_SURROGATE;
    }
}

