package com.chua.pytorch.support.sentence;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.AbstractPytorchIODetector;

import java.util.Collections;
import java.util.List;

/**
 * 句向量提取是指将语句映射至固定维度的实数向量。
 * 将不定长的句子用定长的向量表示，为NLP下游任务提供服务。
 * 支持 15 种语言：
 * Arabic, Chinese, Dutch, English, French, German, Italian, Korean, Polish, Portuguese, Russian, Spanish, Turkish.
 */
public final class SentenceEncoder extends AbstractPytorchIODetector<String, float[]> {

    public SentenceEncoder(DetectionConfiguration configuration) {
        super(configuration,
                new SentenceTransTranslator(),
                "PyTorch",
                null,
                "distiluse-base-multilingual-cased-v1",
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/distiluse-base-multilingual-cased-v1.zip",
                true);
    }

    @Override
    protected Class<float[]> outType() {
        return float[].class;
    }

    @Override
    protected Class<String> inType() {
        return String.class;
    }


    @Override
    public List<PredictResult> detect(Object face) {
        try (Predictor<String, float[]> predictor = model.newPredictor()) {
            float[] predict = predictor.predict(face.toString());
            return Collections.singletonList(new PredictResult().setNdArray(predict));
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }
}
