package com.chua.lucene.support.indexer;

import java.io.IOException;
import java.util.List;

/**
 * 操作器
 *
 * @author CH
 */
public interface Operator<T> {
    /**
     * 更新数据
     *
     * @param index 索引
     * @param id    ID
     * @param data  数据
     * @return 结果
     * @throws IOException 异常
     */
    boolean update(String index, String id, T data) throws IOException;

    /**
     * 删除数据
     *
     * @param index 索引
     * @param id    ID
     * @return 结果
     * @throws IOException 异常
     */
    boolean deleteById(String index, String id) throws IOException;

    /**
     * 添加数据
     *
     * @param index 索引
     * @param data  数据
     * @return 结果
     * @throws IOException 异常
     */
    boolean add(String index, List<T> data) throws IOException;
}
