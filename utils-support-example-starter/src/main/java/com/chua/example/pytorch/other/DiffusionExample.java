package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.tokenizer.Word;
import com.chua.common.support.utils.BufferedImageUtils;
import com.chua.paddlepaddle.support.ocr.tokenizer.LacTokenizer;
import com.chua.paddlepaddle.support.translation.EnglishTranslation;
import com.chua.pytorch.support.diffusion.Diffusion;

import java.awt.image.BufferedImage;
import java.util.List;

public class DiffusionExample {


    public static void main(String[] args) throws Exception {
        String input = "1";
        LacTokenizer lacTokenizer = new LacTokenizer(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());
        List<Word> segments = lacTokenizer.segments(input);
        try (EnglishTranslation translation = new EnglishTranslation(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build())) {
            List<PredictResult> detect = translation.predict(new String[]{input});
            String text = detect.get(0).getText();
            text = "woman";
            Diffusion diffusion = new Diffusion(DetectionConfiguration.builder().cachePath("E:\\workspace\\environment").build());
            List<PredictResult> detect1 = diffusion.predict(text);
            BufferedImageUtils.writeToFile(detect1.get(0).getValue(BufferedImage.class), "Z://1.png");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
