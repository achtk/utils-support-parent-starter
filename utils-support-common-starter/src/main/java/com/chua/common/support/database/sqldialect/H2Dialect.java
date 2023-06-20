package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;

/**
 * h2
 *
 * @author CH
 */
@Spi({"h2"})
public class H2Dialect extends MysqlDialect {
    @Override
    public String driverClassName() {
        return "org.h2.Driver";
    }
}
