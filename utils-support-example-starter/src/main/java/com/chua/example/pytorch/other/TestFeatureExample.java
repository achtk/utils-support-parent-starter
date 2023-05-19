package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.feature.TextFeature;

/**
 * @author CH
 */
public class TestFeatureExample {

    public static void main(String[] args) {
        TextFeature textFeature = new TextFeature(DetectionConfiguration.builder().build());
        float[] predict = textFeature.predict("這個");
        System.out.println(predict);
    }
}
