package com.chua.paddlepaddle.support.review;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.AbstractPytorchIODetector;

import java.util.Collections;
import java.util.List;

/**
 * 文本审查
 *
 * @author CH
 */
public class Review extends AbstractPytorchIODetector<String, float[]> {
    public Review(DetectionConfiguration configuration) {
        super(configuration,
                new ReviewTranslator(),
                "PaddlePaddle",
                null,
                "review_detection_lstm",
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/nlp_models/review_detection_lstm.zip",
                true);
    }

    @Override
    public List<PredictResult> detect(Object face) {
        try (Predictor<String, float[]> predictor = model.newPredictor()) {
            float[] o = predictor.predict((String) face);
            return Collections.singletonList(new PredictResult().setText((String) face).setScore(o[0]));
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<float[]> outType() {
        return float[].class;
    }

    @Override
    protected Class<String> inType() {
        return String.class;
    }
}
