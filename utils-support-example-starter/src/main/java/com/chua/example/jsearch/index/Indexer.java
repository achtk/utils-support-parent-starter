package com.chua.example.jsearch.index;

import org.apdplat.word.segmentation.SegmentationAlgorithm;

/**
 * 索引接口
 * @author 杨尚川
 */
public interface Indexer {
    /**
     * 为目录及其所有子目录下的所有文本建立索引, 默认使用纯英文分词器
     * @param dir 目录
     */
    void indexDir(String dir);
    void indexDir(String dir, SegmentationAlgorithm segmentationAlgorithm);
}
