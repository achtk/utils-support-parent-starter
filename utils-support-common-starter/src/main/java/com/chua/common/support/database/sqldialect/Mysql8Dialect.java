package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;

/**
 * mysql
 *
 * @author CH
 */
@Spi({"mysql8"})
public class Mysql8Dialect extends MysqlDialect {
    @Override
    public String driverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }
}
