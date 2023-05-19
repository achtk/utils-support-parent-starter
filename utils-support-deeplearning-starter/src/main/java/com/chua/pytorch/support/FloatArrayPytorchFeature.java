package com.chua.pytorch.support;

import ai.djl.modality.cv.Image;
import ai.djl.translate.Translator;
import com.chua.common.support.feature.DetectionConfiguration;

/**
 * 特征值
 *
 * @author CH
 */
public abstract class FloatArrayPytorchFeature<O> extends FloatArrayPytorchIOFeature<Image, O> {

    public FloatArrayPytorchFeature(DetectionConfiguration configuration,
                                    Translator<Image, O> translator,
                                    String model,
                                    String defaultModel,
                                    boolean isDirector) {
        this(configuration, translator, "PyTorch", "", model, defaultModel, isDirector);

    }

    public FloatArrayPytorchFeature(DetectionConfiguration configuration,
                                    Translator<Image, O> translator,
                                    String engine,
                                    String modelName,
                                    String model,
                                    String defaultModel,
                                    boolean isDirector) {
        super(configuration, translator, engine, modelName, model, defaultModel, isDirector);

    }

    protected abstract Class<O> type();

    @Override
    protected Class<O> outType() {
        return type();
    }

    @Override
    protected Class<Image> inType() {
        return Image.class;
    }
}
