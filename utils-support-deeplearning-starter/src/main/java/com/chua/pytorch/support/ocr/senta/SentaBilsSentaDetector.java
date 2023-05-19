package com.chua.pytorch.support.ocr.senta;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.lang.tokenizer.Word;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchIODetector;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * Senta
 *
 * @author CH
 */
@Slf4j
public class SentaBilsSentaDetector extends AbstractPytorchIODetector<String[], float[]> {

    private Tokenizer tokenizer;

    public SentaBilsSentaDetector(DetectionConfiguration configuration, Tokenizer tokenizer) {
        super(configuration,
                new SentaTranslator(),
                "PaddlePaddle",
                null,
                //            .optModelUrls("/Users/calvin/model/senta_bilstm/")
                StringUtils.defaultString(configuration.modelPath(), "senta_bilstm"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/nlp_models/senta_bilstm.zip",
                true);
        this.tokenizer = tokenizer;
    }

    @Override
    public List<PredictResult> detect(Object face) {
        List<PredictResult> results = new LinkedList<>();

        List<Word> segments1 = tokenizer.segments(face.toString());
        String[] lacResult1 = segments1.stream().map(Word::getWord).toArray(String[]::new);

        try (Predictor<String[], float[]> predictor = model.newPredictor()) {
            float[] simResult1 = null;
            try {
                simResult1 = predictor.predict(lacResult1);
            } catch (TranslateException e) {
                throw new RuntimeException(e);
            }
            if (log.isDebugEnabled()) {
                log.debug("短文本 1: {}", face);
                log.debug("相似度 : {}", simResult1);
            }

            results.add(new PredictResult("negative", simResult1[0]));
            results.add(new PredictResult("positive", simResult1[1]));
        }

        return results;
    }

    @Override
    protected Class<float[]> outType() {
        return float[].class;
    }

    @Override
    protected Class<String[]> inType() {
        return String[].class;
    }
}
