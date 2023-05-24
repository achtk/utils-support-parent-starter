package com.chua.lucene.support.operator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

import java.util.List;
import java.util.Set;

/**
 * 索引模板
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/3
 */
public interface IndexOperatorTemplate extends AutoCloseable{
    /**
     * 创建索引
     *
     * @param name          索引名称
     * @param fragmentation 分片
     * @throws Exception Exception
     */
    void create(String name, int fragmentation) throws Exception;

    /**
     * 创建索引
     *
     * @param name 索引名称
     * @throws Exception Exception
     */
    default void create(String name) throws Exception {
        create(name, 1);
    }

    /**
     * 删除索引
     *
     * @param name 索引名称
     * @throws Exception Exception
     */
    void delete(String name) throws Exception;

    /**
     * 获取存储的索引真实名称
     *
     * @param index 索引
     * @return 索引真实名称
     * @throws Exception Exception
     */
    String getStoreIndexName(String index) throws Exception;

    /**
     * 非异常方式获取存储的索引真实名称
     *
     * @param index 索引
     * @return 索引真实名称
     */
    default String safeStoreIndexName(String index) {
        try {
            return getStoreIndexName(index);
        } catch (Exception e) {
            return index;
        }
    }

    /**
     * 获取索引名称
     *
     * @param index 索引
     * @return 索引名称
     * @throws Exception Exception
     */
    String getRealIndexName(String index) throws Exception;

    /**
     * 非异常方式获取索引名称
     *
     * @param index 索引
     * @return 索引名称
     */
    default String safeRealIndexName(String index) {
        try {
            return getStoreIndexName(index);
        } catch (Exception e) {
            return index;
        }
    }

    /**
     * 索引是否存在
     *
     * @param index 索引
     * @return 索引真实名称
     */
    boolean exist(String index);

    /**
     * 获取写索引
     *
     * @param index 索引
     * @return List
     */
    List<IndexWriter> indexWrite(String index);

    /**
     * 获取读索引
     *
     * @param index 索引
     * @return List
     */
    List<IndexReader> indexReader(String index);

    /**
     * 获取解析器
     *
     * @return Analyzer
     */
    Analyzer getAnalyzer();

    /**
     * 获取索引
     * @return
     */
    Set<String> getCollections();
}
