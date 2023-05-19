package com.chua.pytorch.support.ocr.tokenizer;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.json.Json;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.lang.tokenizer.Word;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchIODetector;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * lac分词器
 *
 * @author CH
 */
public class LacTokenizer extends AbstractPytorchIODetector<String, String[][]> implements Tokenizer {
    public LacTokenizer(DetectionConfiguration configuration) {
        super(configuration,
                new LacTranslator(),
                "PaddlePaddle",
                null,
                StringUtils.defaultString(configuration.modelPath(), "nlp_models_lac"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/nlp_models/lac.zip",
                true);
    }

    @Override
    protected Class<String[][]> outType() {
        return String[][].class;
    }

    @Override
    protected Class<String> inType() {
        return String.class;
    }


    @Override
    public List<PredictResult> detect(Object face) {
        try (Predictor<String, String[][]> predictor = model.newPredictor()) {
            String[][] predict = predictor.predict(face.toString());
            return Collections.singletonList(new PredictResult(Json.toJson(predict)));
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Word> segments(String word) {
        List<Word> words = new LinkedList<>();
        try (Predictor<String, String[][]> predictor = model.newPredictor()) {
            String[][] predict = predictor.predict(word);
            for (int i = 0; i < predict[0].length; i++) {
                String strings1 = predict[0][i];
                String strings2 = predict[1][i];
                words.add(new Word(strings1, strings2));
            }
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
        return words;
    }
}
