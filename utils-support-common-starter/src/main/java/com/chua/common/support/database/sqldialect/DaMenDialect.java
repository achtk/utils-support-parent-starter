package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;

/**
 * 达梦
 *
 * @author CH
 */
@Spi("dm")
public class DaMenDialect extends MysqlDialect {

    @Override
    public String driverClassName() {
        return "dm.jdbc.driver.DmDriver";
    }
}
