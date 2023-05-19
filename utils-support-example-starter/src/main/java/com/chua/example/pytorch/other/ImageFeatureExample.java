package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.feature.ImageFeature;

/**
 * @author CH
 */
public class ImageFeatureExample {

    public static void main(String[] args) {
        ImageFeature imageFeature = new ImageFeature(DetectionConfiguration.builder().build());
        float[] predict = imageFeature.predict("Z:\\works\\resource\\mask6.jpeg");
        System.out.println(predict);
    }
}
