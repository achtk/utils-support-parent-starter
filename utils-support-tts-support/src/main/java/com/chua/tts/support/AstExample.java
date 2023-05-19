package com.chua.tts.support;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.tts.support.longasr.LongSpeedRecognizer;

import java.util.List;

/**
 * @author CH
 */
public class AstExample {

    public static void main(String[] args) throws Exception {
        LongSpeedRecognizer speedRecognizer = new LongSpeedRecognizer(DetectionConfiguration.DEFAULT);
        List<PredictResult> predictResults = speedRecognizer.recognize("Z:/20230316_101443.m4a");
        System.out.println();
    }
}
