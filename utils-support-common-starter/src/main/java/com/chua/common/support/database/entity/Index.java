package com.chua.common.support.database.entity;

import lombok.Data;

/**
 * 索引
 *
 * @author CH
 */
@Data
public class Index {
    /**
     * 字段名称
     *
     */
    String value;

    /**
     * 排序[ASC|DESC]
     *
     */
    String order;

    /**
     * 索引类型["" | UNIQUE]
     *
     */
    String indexType;

    /**
     * 索引类型["" | UNIQUE]
     *
     */
    String comment;
}
