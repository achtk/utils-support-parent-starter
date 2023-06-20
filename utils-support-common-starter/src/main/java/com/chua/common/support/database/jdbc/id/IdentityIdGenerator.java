
package com.chua.common.support.database.jdbc.id;

import com.chua.common.support.database.jdbc.*;
import com.chua.common.support.utils.StringUtils;

import java.sql.Connection;

/**
 * Define an Identity type generator, supported by MySQL, SQL Server, DB2,
 * Derby, Sybase, PostgreSQL
 *
 * @author Yong Zhu
 * @version 1.0.0
 * @since 1.0.0
 */
public class IdentityIdGenerator implements IdGenerator {
    private String table;

    private String column;

    public IdentityIdGenerator(String table, String column) {
        this.table = table;
        this.column = column;
    }

    @Override
    public GenerationType getGenerationType() {
        return GenerationType.IDENTITY;
    }

    @Override
    public String getIdGenName() {
        return "IDENTITY";
    }

    @Override
    public Boolean dependOnAutoIdGenerator() {
        return false;
    }

    @Override
    public Object getNextID(Connection con, Dialect dialect, Type dataType) {
        if (!Boolean.TRUE.equals(dialect.ddlFeatures.getSupportsIdentityColumns())) {
            throw new DialectException("Dialect '" + dialect + "' does not support identity type");
        }
        String sql = null;
        if (Type.BIGINT.equals(dataType)) {
            sql = dialect.ddlFeatures.getIdentitySelectStringBigINT();
        } else {
            sql = dialect.ddlFeatures.getIdentitySelectString();
        }
        if (StringUtils.isEmpty(sql) || DDLFeatures.NOT_SUPPORT.equals(sql)) {
            throw new DialectException("Dialect '" + dialect + "' does not support identity type");
        }
        sql = StringUtils.replaceFirst(sql, "_table__col", new StringBuilder(table).append("_").append(column).toString());
        return JdbcUtil.qryOneObject(con, sql);
    }

    @Override
    public IdGenerator newCopy() {
        return new IdentityIdGenerator(table, column);
    }

    // getter & setter==============
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
