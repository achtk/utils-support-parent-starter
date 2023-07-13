package com.chua.pytorch.support;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Translator;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.detector.AbstractDetector;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.utils.LocationUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import lombok.Getter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 特征值
 *
 * @author CH
 */
public abstract class AbstractPytorchIODetector<I, O> extends AbstractDetector {

    @Getter
    public ZooModel<I, O> model;

    public AbstractPytorchIODetector(DetectionConfiguration configuration,
                                     Translator<I, O> translator,
                                     String model,
                                     String defaultModel,
                                     boolean isDirector) {
        this(configuration, translator, "PyTorch", null, model, defaultModel, isDirector);
    }

    public AbstractPytorchIODetector(DetectionConfiguration configuration,
                                     Translator<I, O> translator,
                                     String modelName,
                                     String model,
                                     String defaultModel,
                                     boolean isDirector) {
        this(configuration, translator, "PyTorch", modelName, model, defaultModel, isDirector);

    }

    public AbstractPytorchIODetector(DetectionConfiguration configuration,
                                     Translator<I, O> translator,
                                     String engine,
                                     String modelName,
                                     String model,
                                     String defaultModel,
                                     boolean isDirector) {
        super(configuration);
        if (null == configuration) {
            return;
        }

        List<String> model1 = LocationUtils.getUrl(StringUtils.defaultString(configuration.modelPath(), model + "," + model + ".zip"), defaultModel, isDirector);
        Criteria.Builder<I, O> imageOBuilder = Criteria.builder()
                .setTypes(inType(), this.outType())
                .optTranslator(translator)
                .optEngine(engine)
                .optProgress(new ProgressBar());
        if(configuration.groupId() != null) {
            imageOBuilder.optGroupId(configuration.groupId());
        } else {
            imageOBuilder.optModelUrls(Joiner.on(',').join(model1));
        }

        if("GPU".equalsIgnoreCase(configuration.device())) {
            imageOBuilder.optDevice(Device.gpu(configuration.deviceId()));
        }


        if (!Strings.isNullOrEmpty(modelName)) {
            imageOBuilder.optModelName(modelName);
        }

        Criteria<I, O> criteria = imageOBuilder.build();

        try {
            this.model = ModelZoo.loadModel(criteria);
        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    @SuppressWarnings("ALL")
    public void detect(Object face, OutputStream outputStream) throws Exception {
    }

    @Override
    public void close() throws Exception {
        model.close();
    }

    /**
     * 输出类型
     *
     * @return 输出类型
     */
    protected abstract Class<O> outType();


    /**
     * 输出类型
     *
     * @return 输出类型
     */
    protected abstract Class<I> inType();


}
