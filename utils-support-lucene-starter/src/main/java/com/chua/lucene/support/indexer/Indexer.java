package com.chua.lucene.support.indexer;

import com.chua.common.support.utils.ThreadUtils;

import java.io.IOException;

/**
 * 索引
 *
 * @author CH
 */
public interface Indexer {
    /**
     * 删除索引
     *
     * @param name 索引名称
     * @return 索引名称
     * @throws IOException 异常
     */
    String deleteIndex(String name) throws IOException;

    /**
     * 创建索引
     *
     * @param name 索引名称
     * @param size 数量
     * @return 索引名称
     * @throws IOException 异常
     */
    String createIndex(String name, int size) throws IOException;

    /**
     * 创建索引
     *
     * @param name 索引名称
     * @return 索引名称
     * @throws IOException 异常
     */
    default String createIndex(String name) throws IOException {
        return createIndex(name, ThreadUtils.processor());
    }
}
