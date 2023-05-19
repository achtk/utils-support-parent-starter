package com.chua.example.pytorch.other;

import com.chua.common.support.business.TestPaperExtraction;

import java.io.File;
import java.io.IOException;

/**
 * @author CH
 */
public class PythorchExample {

    public static void main(String[] args) throws IOException {
        TestPaperExtraction testPaperExtraction = new TestPaperExtraction(new File("Z:/000000000026.jpg"));
        testPaperExtraction.backgroundWhite().write("Z:/000000000026_rm.jpg");
//        YoloDetector detector = new YoloDetector(DetectionConfiguration.builder()
//                .ext("threshold", 0)
//                .modelPath("Z:/best.torchscript")
//                .synset("Z:/rl.yaml")
//                .build());
//        List<PredictResult> detection = detector.detect("Z:/000000000009.jpg");
//        LocationUtils.saveBoundingBoxImage(detection, "Z:/000000000009.jpg", "Z:/000000000009_out.jpg");
//        System.out.println(detection);
    }
}
