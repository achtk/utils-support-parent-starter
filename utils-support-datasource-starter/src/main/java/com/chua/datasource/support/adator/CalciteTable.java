package com.chua.datasource.support.adator;

import org.apache.calcite.schema.Table;

/**
 * CalciteTable
 *
 * @author CH
 */
public interface CalciteTable extends Table {
    /**
     * 表名
     *
     * @return 表名
     */
    String[] name();
}
