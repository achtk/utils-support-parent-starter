package com.chua.example.jsearch.score;

import com.chua.example.jsearch.search.Doc;

import java.util.List;

/**
 * 评分接口
 * @author 杨尚川
 */
@FunctionalInterface
public interface Score {
    /**
     * 文档评分
     * @param doc 文档
     * @param words 分好词的查询关键词
     * @return 分值
     */
    public Float score(Doc doc, List<String> words);
}
