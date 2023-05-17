package com.chua.hanlp.support.tokenizer;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.lang.tokenizer.Word;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.model.crf.CRFSegmenter;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * hanlp
 *
 * @author CH
 */
@Spi("hanlp:crf")
public final class HanlpCrfTokenizer extends AbstractHanlpTokenizer implements Tokenizer {
    @Override
    public List<Word> segments(String sentence) {
        //CRF分词
        //基于CRF模型和BEMS标注训练得到的分词器
        //CRF对新词有很好的识别能力，但是无法利用自定义词典。
        //也不支持命名实体识别，应用场景仅限于新词识别。
        Segment segment = null;
        try {
            segment = new CRFLexicalAnalyzer(new CRFSegmenter());
        } catch (IOException e) {
            e.printStackTrace();
        }
        segment.enablePartOfSpeechTagging(true);
        List<Term> termList = segment.seg(sentence);

        return termList.stream().map(term -> new Word(term.word, term.nature.toString())).collect(Collectors.toList());
    }
}
