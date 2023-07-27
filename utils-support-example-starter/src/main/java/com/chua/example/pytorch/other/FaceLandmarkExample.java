package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.FeatureComparison;
import com.chua.pytorch.support.face.detector.PaddlePaddleFaceDetector;
import com.chua.pytorch.support.face.feature.AmazonFeature;
import com.chua.pytorch.support.face.land.FaceLandmarkDetector;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author CH
 */
public class FaceLandmarkExample {

    public static void main1(String[] args) throws Exception {
        PaddlePaddleFaceDetector faceDetector = new PaddlePaddleFaceDetector(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());
        FaceLandmarkDetector faceLandmark = new FaceLandmarkDetector(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build(), faceDetector);
//        List<PredictResult> detection = faceLandmark.detection("Z:\\works\\resource\\mask3.jpeg");
        faceLandmark.detect("E:\\yolov5\\datasets\\coco128\\images\\train2017\\000000000370.jpg", Files.newOutputStream(Paths.get("Z:\\works\\resource\\masks_out.png")));
        System.out.println();
    }

    public static void main(String[] args) {
        AmazonFeature lightFaceDetector = new AmazonFeature(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());

        System.out.println("相似度: " + FeatureComparison.calculateSimilar(lightFaceDetector.predict("Z:\\face\\kunkun.jpg"), lightFaceDetector.predict("Z:\\face\\kunkun1.jpg")));
    }
}
