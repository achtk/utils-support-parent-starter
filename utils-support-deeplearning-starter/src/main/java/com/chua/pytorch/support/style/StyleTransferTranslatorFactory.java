package com.chua.pytorch.support.style;

import ai.djl.Model;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorFactory;
import ai.djl.util.Pair;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author CH
 */
public class StyleTransferTranslatorFactory implements TranslatorFactory {

    @Override
    public Set<Pair<Type, Type>> getSupportedTypes() {
        return Collections.singleton(new Pair<>(Image.class, Image.class));
    }

    @Override
    public <I, O> Translator<I, O> newInstance(Class<I> input, Class<O> output, Model model, Map<String, ?> arguments) throws TranslateException {
        if (!isSupported(input, output)) {
            throw new IllegalArgumentException("Unsupported input/output types.");
        }
        return (Translator<I, O>) new StyleTransferTranslator();
    }

}