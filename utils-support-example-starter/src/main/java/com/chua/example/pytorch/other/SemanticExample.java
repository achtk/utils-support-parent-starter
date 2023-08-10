package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.paddlepaddle.support.ocr.detector.SimnetBowDetector;
import com.chua.paddlepaddle.support.ocr.tokenizer.LacTokenizer;

/**
 * @author CH
 */
public class SemanticExample {

    public static void main(String[] args) {
        LacTokenizer tokenizer = new LacTokenizer(DetectionConfiguration.builder().build());
        SimnetBowDetector simnetBowDetector = new SimnetBowDetector(DetectionConfiguration.builder().build(), tokenizer);
        String input1 = "这个棋局太难了";
        String input2 = "这个棋局不简单";
        double match = simnetBowDetector.match(input1, input2);
        System.out.println(match);
    }
}
