package com.chua.pytorch.support;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Translator;
import com.chua.common.support.feature.FloatArrayFeature;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.utils.LocationUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.List;

/**
 * 特征值
 *
 * @author CH
 */
public abstract class FloatArrayPytorchIOFeature<I, O> extends FloatArrayFeature {

    protected final ZooModel<I, O> model;

    public FloatArrayPytorchIOFeature(DetectionConfiguration configuration,
                                      Translator<I, O> translator,
                                      String model,
                                      String defaultModel,
                                      boolean isDirector) {
        this(configuration, translator, "PyTorch", null, model, defaultModel, isDirector);

    }

    public FloatArrayPytorchIOFeature(DetectionConfiguration configuration,
                                      Translator<I, O> translator,
                                      String engine,
                                      String modelName,
                                      String model,
                                      String defaultModel,
                                      boolean isDirector) {
        super(configuration);

        List<String> model1 = LocationUtils.getUrl(model + "," + model + ".zip", defaultModel, isDirector);

        Criteria.Builder<I, O> builder = Criteria.builder()
                .setTypes(inType(), outType())
                .optModelUrls(Joiner.on(',').join(model1) )
                .optTranslator(translator)
                .optEngine(engine)
                .optProgress(new ProgressBar());
        if (!Strings.isNullOrEmpty(modelName)) {
            builder.optModelName(modelName);
        }
        Criteria<I, O> criteria = builder.build();
        try {
            this.model = ModelZoo.loadModel(criteria);
        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            throw new RuntimeException(e);
        }

    }

    protected abstract Class<O> outType();

    protected abstract Class<I> inType();
}
