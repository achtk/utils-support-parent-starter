package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.ocr.senta.SentaLstmSentaDetector;
import com.chua.pytorch.support.ocr.tokenizer.LacTokenizer;

import java.util.List;

/**
 * 情感分析
 *
 * @author CH
 */
public class SentaExample {
    public static void main(String[] args) {
        LacTokenizer tokenizer = new LacTokenizer(DetectionConfiguration.builder().build());
        String input = "这家餐厅很好吃";
        SentaLstmSentaDetector detector = new SentaLstmSentaDetector(DetectionConfiguration.builder().build(), tokenizer);
        List<PredictResult> detect = detector.detect(input);
        System.out.println(detect);
    }
}
