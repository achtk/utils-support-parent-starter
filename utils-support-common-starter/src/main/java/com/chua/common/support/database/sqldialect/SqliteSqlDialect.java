package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;

/**
 * sqlite
 * @author CH
 */
@Spi("sqlite")
public class SqliteSqlDialect extends AbstractSqlDialect{
    public SqliteSqlDialect(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    String getTableSql(String schema) {
        return "SELECT name name, '' schema, sql `comment` FROM sqlite_master WHERE type = 'table'";
    }
}
