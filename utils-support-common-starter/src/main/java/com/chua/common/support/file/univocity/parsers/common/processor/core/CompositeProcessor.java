package com.chua.common.support.file.univocity.parsers.common.processor.core;

import com.chua.common.support.file.univocity.parsers.common.AbstractCommonSettings;
import com.chua.common.support.file.univocity.parsers.common.AbstractContext;
import com.chua.common.support.file.univocity.parsers.common.Format;
import com.chua.common.support.file.univocity.parsers.common.ParsingContext;

/**
 * A utility {@link Processor} implementation that facilitates using multiple implementations of {@link Processor} at the
 * same time.
 *
 * @author Administrator
 * @param <C> the tye of the contextual object with information and controls over the current state of the parsing process
 */
public class CompositeProcessor<C extends AbstractContext> implements Processor<C> {

    private final Processor[] processors;

    /**
     * Creates a new {@code CompositeProcessor} with the list of {@link Processor} implementations to be used.
     *
     * @param processors the sequence of {@link Processor} implementations to be used.
     */
    public CompositeProcessor(Processor... processors) {
        this.processors = processors;
    }

    /**
     * Initializes each {@link Processor} used by this class. This is invoked by the parser once, when it is ready to start processing the input.
     *
     * @param context A contextual object with information and controls over the current state of the parsing process
     */
    @Override
    public void processStarted(C context) {
        for (int i = 0; i < processors.length; i++) {
            processors[i].processStarted(context);
        }
    }

    /**
     * Invoked by the parser after all values of a valid record have been processed. All {@link Processor} implementations
     * will have their corresponding {@link Processor#rowProcessed(String[], AbstractContext)} method called with the given row.
     *
     * @param row     the data extracted by the parser for an individual record. Note that:
     *                <ul>
     *                <li>it will never by null. </li>
     *                <li>it will never be empty unless explicitly configured using {@link AbstractCommonSettings#setSkipEmptyLines(boolean)}</li>
     *                <li>it won't contain lines identified by the parser as comments. To disable comment processing set {@link Format#setComment(char)} to '\0'</li>
     *                </ul>
     * @param context A contextual object with information and controls over the current state of the parsing process
     */
    @Override
    public void rowProcessed(String[] row, C context) {
        for (int i = 0; i < processors.length; i++) {
            processors[i].rowProcessed(row, context);
        }
    }


    /**
     * This method will by invoked by the parser once for each {@link Processor} used by this class, after the parsing process stopped and all resources were closed.
     * <p> It will always be called by the parser: in case of errors, if the end of the input us reached, or if the user stopped the process manually using {@link ParsingContext#stop()}.
     *
     * @param context A contextual object with information and controls over the state of the parsing process
     */
    @Override
    public void processEnded(C context) {
        for (int i = 0; i < processors.length; i++) {
            processors[i].processEnded(context);
        }
    }
}