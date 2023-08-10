package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.FeatureComparison;
import com.chua.pytorch.support.sentence.SentenceEncoder;

public class SentenceEncoderExample {

    public static void main(String[] args) {

        SentenceEncoder sentenceEncoder = new SentenceEncoder(DetectionConfiguration.DEFAULT);
        float[] detect = sentenceEncoder.predict("中国");
        float[] detect1 = sentenceEncoder.predict("中国人");
        System.out.println("相似度: " + FeatureComparison.cosineSim(detect, detect1));
    }
}
