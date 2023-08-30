package com.chua.common.support.extra.quickio.api;

/**
 * 查询条件
 * @author CH
 */
public interface FindOptions {
    /**
     * 排序
     * @param fieldName 字段
     * @param value 值
     * @return 查询条件
     */
    FindOptions sort(String fieldName, int value);

    /**
     * 跳过数量
     * @param size 数量
     * @return this
     */
    FindOptions skip(long size);

    /**
     * 限制数量
     * @param size 数量
     * @return this
     */
    FindOptions limit(long size);

    /**
     * 索引
     * @param fieldName 字段
     * @param fieldValue 值
     */
    void index(String fieldName, Object fieldValue);
}
