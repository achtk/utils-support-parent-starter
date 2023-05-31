package com.chua.common.support.database.sqldialect;

import java.util.List;

/**
 * sql diect
 * @author CH
 */
public interface SqlDialect {

    /**
     * 表信息
     * @param schema schema
     * @return 表信息
     */
    List<SqlTable> getSqlTable(String schema);
}
