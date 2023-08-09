package com.chua.paddlepaddle.support.translation;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchIODetector;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 翻译
 *
 * @author CH
 */
public class EnglishTranslation extends AbstractPytorchIODetector<String[], String[]> {


    public EnglishTranslation(DetectionConfiguration configuration) {
        super(configuration,
                new TranslationTranslator(),
                "PaddlePaddle",
                "inference",
                StringUtils.defaultString(configuration.modelPath(), "translation_zh_en"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/nlp_models/translation/translation_zh_en.zip",
                true);
    }

    @Override
    public List<PredictResult> detect(Object face) {
        try (Predictor<String[], String[]> predictor = model.newPredictor()) {
            String[] o = predictor.predict((String[]) face);
            return Arrays.stream(o).map(it -> new PredictResult().setText(it)).collect(Collectors.toList());
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<String[]> outType() {
        return String[].class;
    }

    @Override
    protected Class<String[]> inType() {
        return String[].class;
    }
}
