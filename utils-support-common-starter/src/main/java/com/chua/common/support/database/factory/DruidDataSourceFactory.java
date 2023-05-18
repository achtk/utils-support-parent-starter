package com.chua.common.support.database.factory;

import com.chua.common.support.reflection.Reflect;
import com.chua.common.support.utils.ClassUtils;

import javax.sql.DataSource;

/**
 * 简单DataSource
 *
 * @author CH
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private static final String DRUID = "com.alibaba.druid.pool.DruidDataSource";

    @Override
    @SuppressWarnings("ALL")
    public Class<? extends DataSource> type() {
        return (Class<? extends DataSource>) ClassUtils.forName(DRUID);
    }

    @Override
    public DataSource create(String url, String driver, String username, String password) {
        Object forObject = ClassUtils.forObject(DRUID);
        Reflect<Object> reflect = Reflect.create(forObject);
        reflect.setMethod("setUrl", url);
        reflect.setMethod("setDriverClassName", driver);
        reflect.setMethod("setUsername", username);
        reflect.setMethod("setPassword", password);

        return (DataSource) forObject;
    }

}
