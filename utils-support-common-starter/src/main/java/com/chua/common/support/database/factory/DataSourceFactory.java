package com.chua.common.support.database.factory;

import javax.sql.DataSource;

/**
 * DataSource factory
 *
 * @author CH
 */
public interface DataSourceFactory {
    /**
     * 类型
     *
     * @return 类型
     */
    Class<? extends DataSource> type();

    /**
     * DataSource
     *
     * @param url      地址
     * @param driver   驱动
     * @param username 用户名
     * @param password 密码
     * @return DataSource
     */
    DataSource create(String url, String driver, String username, String password);
}
