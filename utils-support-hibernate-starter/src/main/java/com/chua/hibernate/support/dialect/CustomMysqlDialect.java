package com.chua.hibernate.support.dialect;

import com.chua.common.support.annotations.Spi;
import org.hibernate.dialect.MySQL8Dialect;


@Spi(order = 10)
public class CustomMysqlDialect extends MySQL8Dialect {
    public CustomMysqlDialect() {
        super();
        registerHibernateType(2015, 65535, "longtext");
    }


}