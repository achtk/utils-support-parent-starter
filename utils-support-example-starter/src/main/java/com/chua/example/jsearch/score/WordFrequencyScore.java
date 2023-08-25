package com.chua.example.jsearch.score;

import com.chua.example.jsearch.search.Doc;

import java.util.List;

/**
 * 词频评分组件
 * @author 杨尚川
 */
public class WordFrequencyScore implements Score {

    @Override
    public Float score(Doc doc, List<String> words) {
        return Float.valueOf(doc.getFrequency());
    }
}
