package com.chua.common.support.database.sqldialect;

import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * sql dialect
 * @author CH
 */
public abstract class AbstractSqlDialect implements SqlDialect{

    private final DataSource dataSource;

    public AbstractSqlDialect(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<SqlTable> getSqlTable(String schema) {
        JdbcInquirer jdbcInquirer = new JdbcInquirer(dataSource, true);
        try {
            return jdbcInquirer.query(getTableSql(schema), SqlTable.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * sql
     * @param schema schema
     * @return sql
     */
    abstract String getTableSql(String schema);
}
