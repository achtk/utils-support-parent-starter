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
        OcrDetector ocrDetector = new OcrDetector(DetectionConfiguration.builder().build());
        OcrDirectionDetector directionDetector = new OcrDirectionDetector(DetectionConfiguration.builder().build());

        OcrRecognizer recognizer = new OcrRecognizer(directionDetector, ocrDetector, DetectionConfiguration.builder().build());
        List<PredictResult> recognize = recognizer.recognize("z:/other/1683794344932.jpg");
        for (PredictResult predictResult : recognize) {
            System.out.println(predictResult.getText());
        }
        LocationUtils.saveBoundingBoxImage(recognize, "z:/other/1683794344932.jpg", "Z:/1/1683794344932_ocr.jpg");
        System.out.println();
    }
}
