package com.chua.mxnet.support.image;

import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import com.chua.common.support.feature.DetectionConfiguration;

/**
 * Darknet53Translator
 *
 * @author CH
 */
public class Darknet53Translator extends ImageClassificationTranslator {
    /**
     * Constructs an Image Classification using {@link Builder}.
     *
     * @param builder the data to build with
     */
    public Darknet53Translator(Builder builder) {
        super(builder);
    }

    public Darknet53Translator(DetectionConfiguration configuration) {
        this(ImageClassificationTranslator.builder(configuration.toMap()));
    }
}
