package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.animals.LightAnimalsClassDetector;

import java.util.List;

/**
 * @author CH
 */
public class AnimalsExample {

    public static void main(String[] args) {
        LightAnimalsClassDetector dectection = new LightAnimalsClassDetector(DetectionConfiguration.builder().build());
        List<PredictResult> detection = dectection.detect("Z:\\works\\resource\\f62e02b5eb39e55b4d4c3b91be6dc58.jpg");
        System.out.println();
    }
}
