package com.chua.lucene.support.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * hit数据
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/3
 */
@Getter
@Setter
public class HitData {
    /**
     * 查询总数
     */
    private long hits;
    /**
     * 匹配总数
     */
    private int total;
    /**
     * 匹配结果
     */
    private List<Map<String, Object>> data;
}
