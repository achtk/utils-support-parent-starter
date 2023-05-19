package com.chua.pytorch.support.ocr.detector;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.algorithm.Algorithm;
import com.chua.common.support.lang.algorithm.MatchingAlgorithm;
import com.chua.common.support.lang.exception.NotSupportedException;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.lang.tokenizer.Word;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchIODetector;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * SimnetBow
 *
 * @author CH
 */
@Slf4j
public class SimnetBowDetector extends AbstractPytorchIODetector<String[][], Float> implements MatchingAlgorithm {
    private Tokenizer tokenizer;

    public SimnetBowDetector(DetectionConfiguration configuration, Tokenizer tokenizer) {
        super(configuration,
                new SimnetBowTranslator(),
                "PaddlePaddle",
                null,
                StringUtils.defaultString(configuration.modelPath(), "simnet_bow"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/nlp_models/semantic/simnet_bow.zip",
                true);
        this.tokenizer = tokenizer;
    }

    @Override
    public List<PredictResult> detect(Object face) {
        throw new NotSupportedException();
    }

    @Override
    protected Class<Float> outType() {
        return Float.class;
    }

    @Override
    protected Class<String[][]> inType() {
        return String[][].class;
    }

    @Override
    public double match(String source, String target) {
        List<Word> segments1 = tokenizer.segments(source);
        List<Word> segments2 = tokenizer.segments(target);

        String[] lacResult1 = segments1.stream().map(Word::getWord).toArray(String[]::new);
        String[] lacResult2 = segments2.stream().map(Word::getWord).toArray(String[]::new);
        int maxLength = Math.max(lacResult1.length, lacResult2.length);
        String[][] inputs = new String[2][maxLength];
        inputs[0] = lacResult1;
        inputs[1] = lacResult2;
        try (Predictor<String[][], Float> predictor = model.newPredictor()) {
            Float simResult1 = null;
            try {
                simResult1 = predictor.predict(inputs);
            } catch (TranslateException e) {
                throw new RuntimeException(e);
            }
            if (log.isDebugEnabled()) {
                log.debug("短文本 1: {}", Joiner.on(' ').join(lacResult1));
                log.debug("短文本 2: {}", Joiner.on(' ').join(lacResult2));
                log.debug("相似度 : {}", simResult1);
            }
            return simResult1;
        }
    }

}
