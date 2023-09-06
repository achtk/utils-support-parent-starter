package com.chua.tts.support.waveglow;

import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;

/**
 * 声码器
 *
 * @author CH
 */
public class PytorchWaveGlowEncoder implements WaveGlowEncoder {
    private final ZooModel<NDArray, NDArray> model;

    public PytorchWaveGlowEncoder(DetectionConfiguration configuration) throws Exception {
        System.setProperty("DJL_CACHE_DIR", configuration.cachePath());
        String model1 = StringUtils.defaultString(configuration.modelPath(),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/speech_models/waveGlow.zip");

        Criteria<NDArray, NDArray> criteria =
                Criteria.builder()
                        .setTypes(NDArray.class, NDArray.class)
                        .optModelUrls(model1)
                        .optTranslator(new WaveGlowTranslator())
                        .optEngine("PyTorch")
                        .optDevice(Device.cpu())
                        .optProgress(new ProgressBar())
                        .build();
        this.model = ModelZoo.loadModel(criteria);
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public NDArray predict(NDArray input) throws Exception {
        try (Predictor<NDArray, NDArray> newPredictor = model.newPredictor()) {
            return newPredictor.predict(input);
        }
    }
}
