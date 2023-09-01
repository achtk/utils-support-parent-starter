package com.chua.example.pytorch.other;

import com.chua.common.support.feature.BaseFloatArrayFeature;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.paddlepaddle.support.face.feature.PaddleFeature;

import java.util.Arrays;

public class FeatureExample {

    public static void main(String[] args) throws Exception {
//        FloatArrayFeature feature = new AmazonFeature(DetectionConfiguration.DEFAULT);
//        System.out.println(Arrays.toString(feature.predict("face/mask1.jpeg")));
        BaseFloatArrayFeature paddleFeature = new PaddleFeature(DetectionConfiguration.DEFAULT);
        System.out.println(Arrays.toString(paddleFeature.predict("face/mask1.jpeg")));
    }
}
