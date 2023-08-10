package com.chua.example.pytorch;

import com.chua.arc.support.engine.ArcFaceEngine;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.Feature;
import com.chua.common.support.feature.FeatureComparison;

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

        ArcFaceEngine arcFaceFeature = new ArcFaceEngine(DetectionConfiguration.builder().cachePath("Z:\\works\\environment").build());
        Feature<byte[]> feature = arcFaceFeature.get(Feature.class);

        byte[] predict = feature.predict(new File("Z:\\works\\data\\c-profile-7-designify.jpg"));
        byte[] predict1 = feature.predict(new File("Z:\\works\\data\\20230808_8139568638251905970.jpg"));
        System.out.println(FeatureComparison.calculateSimilar(predict1, predict));
    }
}
