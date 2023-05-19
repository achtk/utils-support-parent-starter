package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.tokenizer.Word;
import com.chua.pytorch.support.ocr.tokenizer.LacTokenizer;
import com.chua.pytorch.support.translation.EnglishTranslation;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class TranslationExample {

    public static void main(String[] args) throws IOException {
//        Projects.installDependency("paddle", Projects.Dependency.builder().build());

        String input = "今天天气怎么样？";
        LacTokenizer lacTokenizer = new LacTokenizer(DetectionConfiguration.DEFAULT);
        log.info("输入句子: {}", input);

        List<Word> segments = lacTokenizer.segments(input);
        // 分词
        log.info("Words : {}", segments);

        try (EnglishTranslation translation = new EnglishTranslation(DetectionConfiguration.DEFAULT)) {
            System.out.println(translation.detect(segments.stream().map(Word::getWord).toArray(String[]::new)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
