package com.chua.common.support.database.dialect;


import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.metadata.Metadata;

import javax.sql.DataSource;
import java.util.List;

/**
 * delegate
 * @author CH
 */
public class DelegateDialect implements Dialect{
    @Override
    public String protocol() {
        return null;
    }

    @Override
    public String driverClassName() {
        return null;
    }

    @Override
    public String toJdbcType(Class<?> javaType) {
        return null;
    }

    @Override
    public Class<?> toJavaType(String jdbcType) {
        return null;
    }

    @Override
    public int getDefaultLength(String jdbcType) {
        return 0;
    }

    @Override
    public List<Metadata<?>> toMetaData(DataSource dataSource) {
        return null;
    }

    @Override
    public void createTable(Metadata<?> metadata, DataSource dataSource) {

    }

    @Override
    public void dropTable(Metadata<?> metadata, DataSource dataSource) {

    }

    @Override
    public void dropColumn(Metadata<?> metadata, List<Column> column, DataSource dataSource) {

    }

    @Override
    public void addColumn(Metadata<?> metadata, List<Column> column, DataSource dataSource) {

    }

    @Override
    public void modifyColumns(Metadata<?> metadata, List<Column> column, DataSource dataSource) {

    }

    @Override
    public String getPageSql(String querySql, long offset, long limit) {
        return null;
    }

    @Override
    public String createPrimaryKey(Metadata<?> metadata) {
        return null;
    }

    @Override
    public void createPrimaryKey(StringBuffer stringBuffer, Column column) {

    }

    @Override
    public String dialect() {
        return "org.hibernate.dialect.MySQLDialect";
    }

    @Override
    public Object getHibernateDialect() {
        return null;
    }

}
