package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;

/**
 * mysql
 * @author CH
 */
@Spi({"mysql", "h2"})
public class MysqlSqlDialect extends AbstractSqlDialect{
    public MysqlSqlDialect(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    String getTableSql(String schema) {
        StringBuilder stringBuilder = new StringBuilder("SELECT table_name `NAME`, table_schema `SCHEMA`, create_time, table_comment `COMMENT`  FROM \tinformation_schema.`TABLES` WHERE 1 = 1 and TABLE_TYPE = 'BASE TABLE'");
        if(StringUtils.isNotEmpty(schema)) {
            stringBuilder.append(" AND table_schema = '").append(schema).append("'");
        }

        return stringBuilder.toString();
    }
}
