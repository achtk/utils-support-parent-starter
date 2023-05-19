package com.chua.common.support.database.structure;

import lombok.Data;

/**
 * 结构
 *
 * @author CH
 */
@Data
public class Structure {
    /**
     * 名称
     */
    private String columnName;
    /**
     * 名称
     */
    private String columnType;
    /**
     * 类型
     */
    private String dataType;
    /**
     * 字段精度
     */
    private float numericPrecision;
    /**
     * 数字比例尺
     */
    private float numericScale;
    /**
     * 字段大小
     */
    private float columnSize;
    /**
     * 是否主键
     */
    private String columnKey;
}
