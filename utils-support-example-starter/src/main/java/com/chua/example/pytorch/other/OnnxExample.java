package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.onnx.OnnxDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.List;

/**
 * @author CH
 */
public class OnnxExample {

    public static void main(String[] args) {
        OnnxDetector detector = new OnnxDetector(DetectionConfiguration.builder()
                .modelPath("Z:\\works\\env\\yolov5-7.0\\runs\\train\\exp\\weights\\best.onnx")
                .synset("Z:\\works\\env\\yolov5-7.0\\data\\coco128.yaml")
                .build());
        List<PredictResult> detection = detector.detect("Z:\\works\\resource\\000000000138.jpg");
        LocationUtils.saveBoundingBoxImage(detection, "Z:\\works\\resource\\000000000138.jpg", "Z:\\works\\resource\\000000000138_out.jpg");
        System.out.println(detection);
    }
}
