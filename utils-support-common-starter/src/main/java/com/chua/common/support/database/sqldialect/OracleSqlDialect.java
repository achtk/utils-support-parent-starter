package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;

/**
 * oracle
 * @author CH
 */
@Spi("oracle")
public class OracleSqlDialect extends AbstractSqlDialect{
    public OracleSqlDialect(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    String getTableSql(String schema) {
        String sql = "select table_name, owner schema, table_comment `comment` from all_tables";
        if(StringUtils.isNotEmpty(schema)) {
            sql += " WHERE owner = '" + schema + "'";
        }
        return sql;
    }
}
