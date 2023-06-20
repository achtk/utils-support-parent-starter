package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.SqlModel;

/**
 * XCloud
 *
 * @author CH
 */
@Spi("XCloud")
public class XCloudDialect extends OracleDialect {
    @Override
    public SqlModel formatPageSql(String originalSql, int offset, int limit) {
        StringBuilder sql = new StringBuilder(originalSql).append(" LIMIT ");
        if (offset != 0L) {
            sql.append(" (?, ?) ");
            return new SqlModel(sql.toString(), offset + 1, offset + limit);
        }
        sql.append("?");
        return new SqlModel(sql.toString(), limit);
    }

    @Override
    public String driverClassName() {
        return null;
    }
}
