package com.chua.hanlp.support.tokenizer;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.lang.tokenizer.Word;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * hanlp
 *
 * @author CH
 * @since 2021-12-02
 */
@Spi("hanlp:index")
public final class HanlpIndexTokenizer extends AbstractHanlpTokenizer implements Tokenizer {
    @Override
    public List<Word> segments(String sentence) {
        //1、 采用HanLP中文自然语言处理中标准分词进行分词
        List<Term> termList = IndexTokenizer.segment(sentence);

        //上面控制台打印信息就是这里输出的

        return termList.stream().map(term -> new Word(term.word, term.nature.toString())).collect(Collectors.toList());
    }
}
