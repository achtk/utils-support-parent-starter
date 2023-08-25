package com.chua.example.jsearch;

import com.chua.example.jsearch.index.TextIndexer;
import com.chua.example.jsearch.score.WordFrequencyScore;
import com.chua.example.jsearch.search.Hits;
import com.chua.example.jsearch.search.SearchMode;
import com.chua.example.jsearch.search.TextSearcher;
import org.apdplat.word.segmentation.SegmentationAlgorithm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CH
 */
public class SearchExample {

    public static void main(String[] args) {
        //存储索引的文件
        String index = "data/index";
        //存储以行为单位的原文的文件
        String indexText = "data/index_text";
        int indexLengthLimit = 1000;
        //textPath可以为目录也可以为文件
        String textPath = "data/original/text";
        TextIndexer textIndexer = new TextIndexer(index, indexText, indexLengthLimit);
        textIndexer.indexDir(textPath);

        int pageSize = 100;
        TextSearcher textSearcher = new TextSearcher(index, indexText, SegmentationAlgorithm.FullSegmentation);
        textSearcher.setPageSize(pageSize);
        textSearcher.setScore(new WordFrequencyScore());
        Hits hits = textSearcher.search("hive function", SearchMode.INTERSECTION);
        System.out.println("搜索结果数："+hits.getHitCount());
        AtomicInteger j = new AtomicInteger();
        hits.getDocs().forEach(doc -> System.out.println("Result" + j.incrementAndGet() + "、ID：" + doc.getId() + "，Score：" + doc.getScore() + "，Text：" + doc.getText()));
    }
}
