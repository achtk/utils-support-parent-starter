package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.SqlModel;

/**
 * InformixDialect
 *
 * @author CH
 */
@Spi("Informix")
public class InformixDialect extends OracleDialect {

    @Override
    public SqlModel formatPageSql(String originalSql, int offset, int limit) {
        StringBuilder ret = new StringBuilder();
        ret.append(String.format("select skip %s first %s ", offset + "", limit + ""));
        ret.append(originalSql.replaceFirst("(?i)select", ""));
        return new SqlModel(ret.toString());
    }

    @Override
    public String getProtocol() {
        return "Informix";
    }

    @Override
    public String driverClassName() {
        return "com.informix.jdbc.IfxDriver";
    }
}
