package com.chua.lucene.support.indexer;

import java.util.List;

/**
 * 查询器
 *
 * @author CH
 */
public interface Searcher<T> {
    /**
     * 查询数据
     *
     * @param index    索引
     * @param wildcard 查询语句
     * @return 数据
     */
    List<T> wildcard(String index, String wildcard);

    /**
     * 查询数据
     *
     * @param index       索引
     * @param wildcard    查询语句
     * @param highlighter 高亮
     * @return 数据
     */
    List<T> wildcard(String index, String wildcard, String[] highlighter);

    /**
     * 查询数据
     *
     * @param index 索引
     * @param query 查询语句
     * @return 数据
     */
    List<T> query(String index, String query);

    /**
     * 查询数据
     *
     * @param index 索引
     * @param id    id
     * @return 数据
     */
    T findById(String index, String id);
}
