package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.paddlepaddle.support.animals.AnimalsClassDetector;

import java.util.List;

/**
 * @author CH
 */
public class AnimalsExample {

    public static void main(String[] args) {
        AnimalsClassDetector dectection = new AnimalsClassDetector(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());
        List<PredictResult> detection = dectection.predict("Z:\\works\\resource\\000000000312.jpg");
        System.out.println();
    }
}
