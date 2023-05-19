package com.chua.common.support.database.subtable;


import com.chua.common.support.annotations.Spi;

import javax.sql.DataSource;

/**
 * 分表
 *
 * @author CH
 */
@Spi("sharding")
public interface SubTableFactory {

    /**
     * ds
     *
     * @return ds
     */
    DataSource dataSource();

    /**
     * 生成子表
     *
     * @param sourceTable 原始表名
     * @param targetTable 目标表名
     */
    void createSubTable(String sourceTable, String targetTable);
}
