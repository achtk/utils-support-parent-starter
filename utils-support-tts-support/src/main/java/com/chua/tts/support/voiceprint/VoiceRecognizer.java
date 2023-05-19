package com.chua.tts.support.voiceprint;

import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.FeatureComparison;
import com.chua.common.support.lang.algorithm.MatchingAlgorithm;
import com.chua.common.support.lang.profile.DelegateProfile;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

/**
 * 声纹
 *
 * @author CH
 */
public class VoiceRecognizer extends DelegateProfile implements MatchingAlgorithm, AutoCloseable {
    private final ZooModel<float[][], float[]> model;

    private final VoiceFeature voiceFeature = new VoiceFeature();

    public VoiceRecognizer(DetectionConfiguration configuration) throws Exception {
        System.setProperty("DJL_CACHE_DIR", configuration.cachePath());
        String model1 = StringUtils.defaultString(configuration.modelPath(),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/speech_models/voiceprint.zip");

        Criteria<float[][], float[]> criteria =
                Criteria.builder()
                        .setTypes(float[][].class, float[].class)
                        .optModelUrls(model1)
                        // .optModelUrls(Paths.get("src/main/resources/voice/"))
                        .optTranslator(new VoiceTranslator())
                        .optEngine("PaddlePaddle")
                        .optProgress(new ProgressBar())
                        .build();

        this.model = ModelZoo.loadModel(criteria);

    }


    @Override
    public void close() throws Exception {
        IoUtils.closeQuietly(model);
    }

    @Override
    public double match(String source, String target) {
        float[][] predict = voiceFeature.predict(source);
        float[][] predict1 = voiceFeature.predict(target);
        try (Predictor<float[][], float[]> predictor = model.newPredictor()) {
            return FeatureComparison.cosineSim(predictor.predict(predict), predictor.predict(predict1));
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }

    }

}
