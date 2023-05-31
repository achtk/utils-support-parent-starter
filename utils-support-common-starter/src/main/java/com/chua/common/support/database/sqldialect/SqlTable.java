package com.chua.common.support.database.sqldialect;

import lombok.Data;

/**
 *
 * table
 * @author CH
 */
@Data
public class SqlTable {
    /**
     * 表名
     */
    private String name;
    /**
     * schema
     */
    private String schema;
    /**
     * comment
     */
    private String comment;
    /**
     * create time
     */
    private String createTime;
}
