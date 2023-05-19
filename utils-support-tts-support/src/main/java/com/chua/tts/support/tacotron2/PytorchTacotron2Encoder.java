package com.chua.tts.support.tacotron2;

import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

/**
 * 频谱数据
 *
 * @author CH
 */
public class PytorchTacotron2Encoder implements Tacotron2Encoder {
    private final ZooModel<NDList, NDArray> model;

    public PytorchTacotron2Encoder(DetectionConfiguration configuration) throws Exception {
        System.setProperty("DJL_CACHE_DIR", configuration.cachePath());
        String model1 = StringUtils.defaultString(StringUtils.defaultString(configuration.modelPath(), "tacotron2"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/speech_models/tacotron2.zip");

        Criteria<NDList, NDArray> criteria =
                Criteria.builder()
                        .setTypes(NDList.class, NDArray.class)
                        .optModelUrls(model1)
                        .optTranslator(new TacotronTranslator())
                        .optEngine("PyTorch")
                        .optProgress(new ProgressBar())
                        .build();

        this.model = ModelZoo.loadModel(criteria);

    }

    @Override
    public void close() throws Exception {
        IoUtils.closeQuietly(model);
    }

    @Override
    public NDArray predict(NDList input) throws Exception {
        try (Predictor<NDList, NDArray> newPredictor = model.newPredictor()) {
            return newPredictor.predict(input);
        }
    }
}
