package com.chua.lucene.support.entity;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

/**
 * 数据文档
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/3
 */
@Data
public class DataDocument {
    public static final String UNIQUELY_IDENTIFIES = "dataId";
    /**
     * 数据唯一标识(默认:UUID)
     */
    private String dataId = UUID.randomUUID().toString();
    /**
     * 数据信息
     */
    private Map<String, Object> data;
}
