package com.chua.common.support.lang.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Executes a sequence of translators one after the other. Execution ends whenever
 * the first translator consumes code points from the input.
 *
 * @since 1.0
 */
public class AggregateTranslator extends AbstractCharSequenceTranslator {

    /**
     * Translator list.
     */
    private final List<AbstractCharSequenceTranslator> translators = new ArrayList<>();

    /**
     * Specify the translators to be used at creation time.
     *
     * @param translators CharSequenceTranslator array to aggregate
     */
    public AggregateTranslator(final AbstractCharSequenceTranslator... translators) {
        if (translators != null) {
            Stream.of(translators).filter(Objects::nonNull).forEach(this.translators::add);
        }
    }

    /**
     * The first translator to consume code points from the input is the 'winner'.
     * Execution stops with the number of consumed code points being returned.
     * {@inheritDoc}
     */
    @Override
    public int translate(final CharSequence input, final int index, final Writer writer) throws IOException {
        for (final AbstractCharSequenceTranslator translator : translators) {
            final int consumed = translator.translate(input, index, writer);
            if (consumed != 0) {
                return consumed;
            }
        }
        return 0;
    }

}