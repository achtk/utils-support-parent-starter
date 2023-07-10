package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;

/**
 * sqlite
 *
 * @author CH
 */
@Spi({"sqlite"})
public class SqliteDialect extends MysqlDialect {
    @Override
    public String driverClassName() {
        return "org.sqlite.JDBC";
    }
    @Override
    public String getProtocol() {
        return "Sqlite";
    }


}
