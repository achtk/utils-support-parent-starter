package com.chua.lucene.support.entity;


import lombok.Data;

import java.util.List;

/**
 * 查询对象
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
@Data
public class Search {
    /**
     * 查询条件
     */
    private String search;
    /**
     * 最大查询数量
     */
    private int max = 10;
    /**
     * 查询字段
     */
    private List<String> fields;
    /**
     * 匹配模式
     */
    private Match match = Match.FIELD;

    /**
     * 匹配模式
     */
    public enum Match {
        /**
         * 字段匹配
         */
        FIELD,
        /**
         * 全部匹配
         */
        FULL
    }
}