package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.mxnet.support.action.InceptionV3ActionRecognizer;

import java.util.List;

public class ActionExample {

    public static void main(String[] args) {
        InceptionV3ActionRecognizer v3ActionRecognizer = new InceptionV3ActionRecognizer(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());
        List<PredictResult> detect = v3ActionRecognizer.predict("underwater_images/action.jpeg");
        System.out.println();
    }
}
