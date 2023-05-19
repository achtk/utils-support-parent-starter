package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.coco.CocoDetector;

import java.util.List;

/**
 * @author CH
 */
public class CocoExample {

    public static void main(String[] args) {
        CocoDetector dectection = new CocoDetector(DetectionConfiguration.builder().modelPath("Z:\\best.torchscript").build());
        List<PredictResult> detection = dectection.detect("Z://000000000009.jpg");
        System.out.println(detection);
    }
}
