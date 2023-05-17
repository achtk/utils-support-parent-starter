package com.chua.common.support.lang.tokenizer.jieba;

import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.lang.tokenizer.Word;
import com.chua.common.support.lang.tokenizer.jieba.huaban.analysis.jieba.JiebaSegmenter;
import com.chua.common.support.lang.tokenizer.jieba.huaban.analysis.jieba.SegToken;

import java.util.List;
import java.util.stream.Collectors;

/**
 * jieba分词
 *
 * @author CH
 */
@SpiDefault
public class JiebaTokenizer implements Tokenizer {

    final JiebaSegmenter segmenter = new JiebaSegmenter();

    @Override
    public List<Word> segments(String word) {
        List<SegToken> process = segmenter.process(word, JiebaSegmenter.SegMode.SEARCH);
        return process.stream().map(it -> {
            Word word1 = new Word();
            word1.setWord(it.word);
            word1.setOffset(it.startOffset);
            word1.setEnd(it.endOffset);
            return word1;
        }).collect(Collectors.toList());
    }
}
