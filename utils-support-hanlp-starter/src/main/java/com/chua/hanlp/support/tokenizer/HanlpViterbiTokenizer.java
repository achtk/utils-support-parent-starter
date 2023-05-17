package com.chua.hanlp.support.tokenizer;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.lang.tokenizer.Word;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;
import java.util.stream.Collectors;

/**
 * hanlp
 *
 * @author CH
 * @since 2021-12-02
 */
@Spi("hanlp:viterbi")
public final class HanlpViterbiTokenizer extends AbstractHanlpTokenizer implements Tokenizer {

    private final Segment shortestSegment = new ViterbiSegment()
            .enableCustomDictionary(false)
            .enablePlaceRecognize(true)
            .enableOrganizationRecognize(true);


    @Override
    public List<Word> segments(String sentence) {
        //1、 采用HanLP中文自然语言处理中标准分词进行分词
        List<Term> termList = shortestSegment.seg(sentence);

        //上面控制台打印信息就是这里输出的

        return termList.stream().map(term -> new Word(term.word, term.nature.toString())).collect(Collectors.toList());
    }
}
