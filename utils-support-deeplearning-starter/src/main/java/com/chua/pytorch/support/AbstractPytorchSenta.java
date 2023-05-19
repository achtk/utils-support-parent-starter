package com.chua.pytorch.support;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Translator;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.senta.AbstractSenta;
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
public abstract class AbstractPytorchSenta<O> extends AbstractSenta {

    protected final ZooModel<String[], O> model;

    public AbstractPytorchSenta(DetectionConfiguration configuration,
                                Translator<String[], O> translator,
                                String model,
                                String defaultModel,
                                boolean isDirector) {
        this(configuration, translator, "PaddlePaddle", null, model, defaultModel, isDirector);

    }

    public AbstractPytorchSenta(DetectionConfiguration configuration,
                                Translator<String[], O> translator,
                                String modelName,
                                String model,
                                String defaultModel,
                                boolean isDirector) {
        this(configuration, translator, "PaddlePaddle", modelName, model, defaultModel, isDirector);

    }

    public AbstractPytorchSenta(DetectionConfiguration configuration,
                                Translator<String[], O> translator,
                                String engine,
                                String modelName,
                                String model,
                                String defaultModel,
                                boolean isDirector) {
        super(configuration);

        List<String> model1 = LocationUtils.getUrl(model, defaultModel, isDirector);
        Criteria.Builder<String[], O> imageOBuilder = Criteria.builder()
                .setTypes(String[].class, type())
                .optModelUrls(Joiner.on(',').join(model1))
                .optTranslator(translator)
                .optEngine(engine)
                .optProgress(new ProgressBar());
        if (!Strings.isNullOrEmpty(modelName)) {
            imageOBuilder.optModelName(modelName);
        }

        Criteria<String[], O> criteria = imageOBuilder.build();

        try {
            this.model = ModelZoo.loadModel(criteria);
        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws Exception {
        model.close();
    }

    protected abstract Class<O> type();

}
