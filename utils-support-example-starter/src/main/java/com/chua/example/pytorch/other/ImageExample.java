package com.chua.example.pytorch.other;

import com.chua.arc.support.engine.ArcFaceEngine;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.detector.Detector;

/**
 * @author CH
 */
public class ImageExample {

    public static void main(String[] args) throws Exception {
//        ImageTrain train = new ImageTrain(224, 224);
//        train.modelName("car").numEpoch(10);
//        train.train("Z:\\works\\resource\\Cars_320", "D:/");
//        ImageDetector detection = new ImageDetector(224, 224, "D:/");
//        detection.modelName("car");
//        detection.afterPropertiesSet();
//
//        List<PredictResult> detection1 = detection.predict("Z:\\works\\resource\\car1.png");
//        System.out.println();
        ArcFaceEngine arcFaceEngine = new ArcFaceEngine(DetectionConfiguration.DEFAULT);
        Detector detector = arcFaceEngine.get(Detector.class);
        System.out.println();
    }
}
