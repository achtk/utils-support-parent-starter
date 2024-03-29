package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.tokenizer.Word;
import com.chua.common.support.utils.BufferedImageUtils;
import com.chua.paddlepaddle.support.ocr.tokenizer.LacTokenizer;
import com.chua.paddlepaddle.support.translation.EnglishTranslation;
import com.chua.pytorch.support.diffusion.ImageDiffusion;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImageDiffusionExample {


    public static void main(String[] args) throws Exception {
        String input = "根据这个图片画出三只猫";
        LacTokenizer lacTokenizer = new LacTokenizer(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());
        List<Word> segments = lacTokenizer.segments(input);
        try (EnglishTranslation translation = new EnglishTranslation(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build())) {
            List<PredictResult> detect = translation.predict(segments.stream().map(Word::getWord).toArray(String[]::new));
            String text = detect.get(0).getText();
            ImageDiffusion diffusion = new ImageDiffusion(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());
            List<PredictResult> detect1 = diffusion.predict(new Object[]{"Z:/cat.jpg", text, ""});
            BufferedImageUtils.writeToFile(detect1.get(0).getValue(BufferedImage.class), "Z://1.png");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
