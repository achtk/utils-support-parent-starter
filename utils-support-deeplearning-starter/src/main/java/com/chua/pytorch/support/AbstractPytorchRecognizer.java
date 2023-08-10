package com.chua.pytorch.support;

import ai.djl.MalformedModelException;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Translator;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.detector.Detector;
import com.chua.common.support.feature.recognizer.AbstractRecognizer;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.utils.LocationUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 特征值
 *
 * @author CH
 */
public abstract class AbstractPytorchRecognizer<O> extends AbstractRecognizer {

    protected ZooModel<Image, O> model;
    protected Detector detector;

    public AbstractPytorchRecognizer(Detector detector, DetectionConfiguration configuration,
                                     Translator<Image, O> translator,
                                     String model,
                                     String defaultModel,
                                     boolean isDirector) {
        this(detector, configuration, translator, "PyTorch", null, model, defaultModel, isDirector);
    }

    public AbstractPytorchRecognizer(Detector detector, DetectionConfiguration configuration,
                                     Translator<Image, O> translator,
                                     String modelName,
                                     String model,
                                     String defaultModel,
                                     boolean isDirector) {
        this(detector, configuration, translator, "PyTorch", modelName, model, defaultModel, isDirector);

    }

    public AbstractPytorchRecognizer(Detector detector, DetectionConfiguration configuration,
                                     Translator<Image, O> translator,
                                     String engine,
                                     String modelName,
                                     String model,
                                     String defaultModel,
                                     boolean isDirector) {
        super(configuration);
        this.detector = detector;
        if (null == configuration) {
            return;
        }

        List<String> model1 = LocationUtils.getUrl(StringUtils.defaultString(configuration.modelPath(), model + "," + model + ".zip"), defaultModel, isDirector);
        Criteria.Builder<Image, O> imageOBuilder = Criteria.builder()
                .setTypes(Image.class, type())
                .optModelUrls(Joiner.on(',').join(model1))
                .optTranslator(translator)
                .optEngine(engine)
                .optProgress(new ProgressBar());
        if (!Strings.isNullOrEmpty(modelName)) {
            imageOBuilder.optModelName(modelName);
        }

        Criteria<Image, O> criteria = imageOBuilder.build();

        try {
            this.model = ModelZoo.loadModel(criteria);
        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<PredictResult> predict(Object face) {
        List<PredictResult> predict = detector.predict(face);

        Image image = LocationUtils.getImage(face);

        List<PredictResult> rs = new LinkedList<>();
        for (PredictResult predictResult : predict) {
            PredictResult result = createPredictResult(LocationUtils.getSubImage(image, (BoundingBox) predictResult.getBoundingBox()));
            if (null == result) {
                continue;
            }
            rs.add(result);
        }

        return rs;
    }

    /**
     * 识别子图片
     *
     * @param subImage 子图片
     * @return 结果
     */
    abstract protected PredictResult createPredictResult(Image subImage);


    @Override
    public void close() throws Exception {
        model.close();
        detector.close();
    }

    /**
     * 输出类型
     *
     * @return 输出类型
     */
    protected abstract Class<O> type();


}
