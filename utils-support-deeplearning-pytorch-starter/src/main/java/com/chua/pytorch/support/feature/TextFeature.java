package com.chua.pytorch.support.feature;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.FloatArrayPytorchIOFeature;

/**
 * 特征值
 *
 * @author CH
 */
@Spi("TextFeature")
public class TextFeature extends FloatArrayPytorchIOFeature<String, float[]> {

    public TextFeature(DetectionConfiguration configuration) {
        this(configuration, true);
    }

    public TextFeature(DetectionConfiguration configuration, boolean isChinese) {
        super(configuration,
                new TextTranslator(isChinese),
                StringUtils.defaultString(configuration.modelPath(), "M-BERT-Base-ViT-B"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/nlp_models/clip_series/M-BERT-Base-ViT-B.zip",
                true);
    }

    @Override
    public float[] predict(Object img) {
        try (Predictor<String, float[]> predictor = model.newPredictor()) {
            return predictor.predict(img.toString());
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
