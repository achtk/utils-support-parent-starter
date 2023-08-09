package com.chua.pytorch.support.objects;

import ai.djl.tensorflow.zoo.cv.objectdetction.TfSsdTranslator;
import com.chua.common.support.feature.DetectionConfiguration;

/**
 * Darknet53Translator
 *
 * @author CH
 */
public class ObjectsTranslator extends TfSsdTranslator {
    /**
     * Constructs an Image Classification using {@link Builder}.
     *
     * @param builder the data to build with
     */
    public ObjectsTranslator(Builder builder) {
        super(builder);
    }

    public ObjectsTranslator(DetectionConfiguration configuration) {
        this(TfSsdTranslator.builder(configuration.toMap()));
    }
}
