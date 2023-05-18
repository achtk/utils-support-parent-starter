package com.chua.common.support.table;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 表字段
 *
 * @author CH
 */
@Data
@Builder
@Accessors(fluent = true)
public class ConnectorColumn {
    /**
     * 数据库字段名称
     */
    private String name;
    /**
     * 类型
     */
    @Builder.Default
    private String type = "varchar";
    /**
     * 映射名称
     * <p>e.g.csv: name: NAME, mapping: 0</p>
     */
    private String mapping;
    /**
     * 类型
     */
    private String sType;
}
