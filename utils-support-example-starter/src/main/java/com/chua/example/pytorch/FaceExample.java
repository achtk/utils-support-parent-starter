package com.chua.example.pytorch;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.FeatureComparison;
import com.chua.pytorch.support.arc.ArcFaceEngine;

import java.io.File;

/**
 * @author CH
 */
public class FaceExample {

    public static void main(String[] args) throws Exception {
//        Face face = new Face();
//        face.train("simple.xm", "D:/2", "D:/");
//        List<PredictResult> classify = face.classify("D:/1/12.jpg");
//        face.classify("D:/1/12.jpg", Files.newOutputStream(Paths.get("D:/1/1212.jpg")));
//        face.detection("D:/1/14.jpg", Files.newOutputStream(Paths.get("D:/1/1414.jpg")));
        System.out.println();

        ArcFaceEngine arcFaceFeature = new ArcFaceEngine(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());
        byte[] predict = arcFaceFeature.predict(new File("D:\\1\\2\\c-profile-7-designify.jpg"));
        byte[] predict1 = arcFaceFeature.predict(new File("D:\\1\\2\\20230808_8139568638251905970.jpg"));
        System.out.println(FeatureComparison.calculateSimilar(predict1, predict));
    }
}
