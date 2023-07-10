package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.SqlModel;

/**
 * PostgreDialect
 *
 * @author CH
 */
@Spi("Postgre")
public class PostgreDialect extends OracleDialect {
    @Override
    public SqlModel formatPageSql(String originalSql, int offset, int limit) {
        StringBuilder sql = new StringBuilder(originalSql).append(" LIMIT ?");
        if (offset != 0L) {
            sql.append(" OFFSET ?");
            return new SqlModel(sql.toString(), limit, offset);
        }
        return new SqlModel(sql.toString(), limit);
    }
    @Override
    public String getProtocol() {
        return "PostgreSQL";
    }
    @Override
    public String driverClassName() {
        return "org.postgresql.Driver";
    }
}
