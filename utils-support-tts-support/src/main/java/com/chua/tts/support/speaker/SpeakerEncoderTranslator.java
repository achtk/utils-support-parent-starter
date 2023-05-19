package com.chua.tts.support.speaker;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

/**
 * @author Administrator
 */
public class SpeakerEncoderTranslator implements Translator<NDArray, NDArray> {

    public SpeakerEncoderTranslator() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NDList processInput(TranslatorContext ctx, NDArray input) {
        return new NDList(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NDArray processOutput(TranslatorContext ctx, NDList list) {
        return list.singletonOrThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Batchifier getBatchifier() {
        return Batchifier.STACK;
    }

}
