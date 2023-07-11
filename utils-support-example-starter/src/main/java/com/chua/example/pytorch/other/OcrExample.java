package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.ocr.detector.OcrDetector;
import com.chua.pytorch.support.ocr.recognizer.OcrRecognizer;
import com.chua.pytorch.support.ocr.rotation.OcrDirectionDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.List;

/**
 * @author CH
 */
public class OcrExample {

    public static void main(String[] args) {
        OcrDetector ocrDetector = new OcrDetector(DetectionConfiguration.builder().cachePath("Z:\\works\\environment").build());
        OcrDirectionDetector directionDetector = new OcrDirectionDetector(DetectionConfiguration.builder().cachePath("Z:\\works\\environment").build());

        OcrRecognizer recognizer = new OcrRecognizer(directionDetector, ocrDetector, DetectionConfiguration.builder().cachePath("Z:\\works\\environment").build());
        List<PredictResult> recognize = recognizer.recognize("Z:\\works\\resource\\3.png");
        for (PredictResult predictResult : recognize) {
            System.out.println(predictResult.getText());
        }
        LocationUtils.saveBoundingBoxImage(recognize, "Z:\\works\\resource\\3.png", "Z:\\works\\resource\\3111.png");
        System.out.println();
    }
}
