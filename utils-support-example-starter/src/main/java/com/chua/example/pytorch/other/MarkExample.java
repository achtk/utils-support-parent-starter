package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.face.detector.PaddlePaddleFaceDetector;
import com.chua.pytorch.support.face.mask.PaddlePaddleMaskDetector;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author CH
 */
public class MarkExample {

    public static void main(String[] args) throws Exception {
        PaddlePaddleFaceDetector faceDetector = new PaddlePaddleFaceDetector(DetectionConfiguration.builder().build());
        PaddlePaddleMaskDetector faceLandmark = new PaddlePaddleMaskDetector(DetectionConfiguration.builder().build(), faceDetector);
//        List<PredictResult> detection = faceLandmark.detection("Z:\\works\\resource\\mask3.jpeg");
        faceLandmark.detect("face/mask1.jpeg", Files.newOutputStream(Paths.get("Z:\\works\\resource\\masks_mask_out.png")));
        System.out.println();
    }
}
