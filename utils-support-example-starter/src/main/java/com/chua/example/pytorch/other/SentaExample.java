package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.paddlepaddle.support.ocr.senta.SentaLstmSentaDetector;
import com.chua.paddlepaddle.support.ocr.tokenizer.LacTokenizer;

import java.util.List;

/**
 * 情感分析
 *
 * @author CH
 */
public class SentaExample {
    public static void main(String[] args) {
        LacTokenizer tokenizer = new LacTokenizer(DetectionConfiguration.builder().modelPath("E:\\workspace\\environment").build());
        String input = "这家餐厅很好吃";
        SentaLstmSentaDetector detector = new SentaLstmSentaDetector(DetectionConfiguration.builder().build(), tokenizer);
        List<PredictResult> detect = detector.predict(input);
        System.out.println(detect);
    }
}
