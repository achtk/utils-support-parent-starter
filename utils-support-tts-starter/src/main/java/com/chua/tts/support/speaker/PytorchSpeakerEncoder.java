package com.chua.tts.support.speaker;

import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

/**
 * 声音克隆
 *
 * @author CH
 */
public class PytorchSpeakerEncoder implements SpeakerEncoder {

    private final Criteria<NDArray, NDArray> criteria;
    private final ZooModel<NDArray, NDArray> speakerEncoderModel;

    public PytorchSpeakerEncoder(DetectionConfiguration configuration) throws Exception {
        System.setProperty("DJL_CACHE_DIR", configuration.cachePath());
        String model1 = StringUtils.defaultString(StringUtils.defaultString(configuration.modelPath(), "speakerEncoder"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/speech_models/speakerEncoder.zip");
        this.criteria =
                Criteria.builder()
                        .setTypes(NDArray.class, NDArray.class)
                        .optModelUrls(model1)
                        .optTranslator(new SpeakerEncoderTranslator())
                        .optEngine("PyTorch")
                        .optProgress(new ProgressBar())
                        .build();

        this.speakerEncoderModel = ModelZoo.loadModel(criteria);
    }

    @Override
    public void close() throws Exception {
        IoUtils.closeQuietly(speakerEncoderModel);
    }

    @Override
    public NDArray predict(NDArray ndArray) throws Exception {
        try (Predictor<NDArray, NDArray> newPredictor = speakerEncoderModel.newPredictor()) {
            return newPredictor.predict(ndArray);
        }
    }
}
