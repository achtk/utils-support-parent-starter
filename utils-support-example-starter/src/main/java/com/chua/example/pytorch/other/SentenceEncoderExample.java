package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.FeatureComparison;
import com.chua.pytorch.support.sentence.SentenceEncoder;

import java.util.List;

public class SentenceEncoderExample {

    public static void main(String[] args) {

        SentenceEncoder sentenceEncoder = new SentenceEncoder(DetectionConfiguration.DEFAULT);
        List<PredictResult> detect = sentenceEncoder.detect("中国");
        List<PredictResult> detect1 = sentenceEncoder.detect("中国人");
        System.out.println("相似度: " + FeatureComparison.cosineSim(detect.get(0).getValue(float[].class), detect1.get(0).getValue(float[].class)));
    }
}
