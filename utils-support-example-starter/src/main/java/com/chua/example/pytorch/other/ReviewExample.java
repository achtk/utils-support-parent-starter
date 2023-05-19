package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.review.Review;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReviewExample {

    public static void main(String[] args) {
        Review review = new Review(DetectionConfiguration.DEFAULT);
        String input = "匿名";
        log.info("input Sentence: {}", input);
        log.info("{}", review.detect(input));
        input = "老人家是猪";
        log.info("input Sentence: {}", input);
        log.info("{}", review.detect(input));
    }
}
