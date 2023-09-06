package com.chua.tts.support.asr;

import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.recognizer.AbstractRecognizer;
import com.chua.common.support.utils.StringUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

/**
 * 短语音识别
 *
 * @author CH
 */
public class SpeedRecognizer extends AbstractRecognizer {

    @Getter
    private final ZooModel<NDArray, Pair> model;

    public SpeedRecognizer(DetectionConfiguration configuration) throws Exception {
        super(configuration);
        String model1 = StringUtils.defaultString(configuration.modelPath(),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/speech_models/deep_speech.zip");
        Criteria<NDArray, Pair> criteria =
                Criteria.builder()
                        .setTypes(NDArray.class, Pair.class)
                        .optModelUrls(
                                model1)
                        .optTranslator(new AudioTranslator())
                        .optEngine("PaddlePaddle")

                        .optProgress(new ProgressBar())
                        .build();


        this.model = ModelZoo.loadModel(criteria);
    }

    @Override
    public float[] predict(Object img) {
        return new float[0];
    }

    @SneakyThrows
    @Override
    public List<PredictResult> recognize(Object image) {
        NDManager manager = NDManager.newBaseManager(Device.cpu());
        NDArray audioFeature = AudioProcess.processUtterance(manager, image.toString());
        try (Predictor<NDArray, Pair> predictor = model.newPredictor()) {
            Pair result = predictor.predict(audioFeature);
            return Collections.singletonList(new PredictResult((String) result.getRight(), (Float) result.getLeft()));
        }
    }

    @Override
    public void close() throws Exception {
        model.close();
    }
}
