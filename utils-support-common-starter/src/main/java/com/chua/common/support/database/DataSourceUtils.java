package com.chua.common.support.database;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.database.factory.DelegateDataSource;
import com.chua.common.support.database.sqldialect.Dialect;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.reflection.MethodStation;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.NetAddress;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * datasource
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public final class DataSourceUtils {

    private static final Class<? extends DataSource> HIK = (Class<? extends DataSource>) ClassUtils.forName("com.zaxxer.hikari.HikariDataSource");
    private static final Class<? extends DataSource> DRUID = (Class<? extends DataSource>) ClassUtils.forName("com.alibaba.druid.pool.DruidDataSource");
    private static final Class<? extends DataSource> IBATIS = (Class<? extends DataSource>) ClassUtils.forName("org.apache.ibatis.datasource.pooled.PooledDataSource");
    private static final Class<? extends DataSource> SPRING = (Class<? extends DataSource>) ClassUtils.forName("org.springframework.jdbc.datasource.SimpleDriverDataSource");
    private static final Class<? extends DataSource> SIMPLE = DelegateDataSource.class;

    private static final Map<Class<?>, Map<String, String>> dataSourceAndParam = new LinkedHashMap<>();

    static {
        {
            if (null != HIK) {
                Map<String, String> item = new LinkedHashMap<>();
                item.put("driver", "setDriverClassName");
                item.put("url", "setJdbcUrl");
                item.put("username", "setUsername");
                item.put("password", "setPassword");
                item.put("maxActive", "setMaximumPoolSize");

                dataSourceAndParam.put(HIK, item);
            }
        }

        {
            if (null != DRUID) {
                Map<String, String> item = new LinkedHashMap<>();
                item.put("driver", "setDriverClassName");
                item.put("url", "setJdbcUrl");
                item.put("username", "setUsername");
                item.put("password", "setPassword");
                item.put("maxActive", "setMaxActive");
                dataSourceAndParam.put(DRUID, item);
            }
        }
        {
            if (null != IBATIS) {
                Map<String, String> item = new LinkedHashMap<>();
                item.put("driver", "setDriver");
                item.put("url", "setJdbcUrl");
                item.put("username", "setUsername");
                item.put("password", "setPassword");
                item.put("maxActive", "setPoolMaximumActiveConnections");
                dataSourceAndParam.put(DRUID, item);
            }
        }
        {
            if (null != SPRING) {
                Map<String, String> item = new LinkedHashMap<>();
                item.put("driver", "setDriverClass");
                item.put("url", "setJdbcUrl");
                item.put("username", "setUsername");
                item.put("password", "setPassword");

                dataSourceAndParam.put(DRUID, item);
            }
        }
        {
            Map<String, String> item = new LinkedHashMap<>();
            item.put("driver", "setDriverClass");
            item.put("url", "setJdbcUrl");
            item.put("username", "setUsername");
            item.put("password", "setPassword");

            dataSourceAndParam.put(SIMPLE, item);
        }
    }

    /**
     * 初始化H2數據源
     *
     * @param name 名称
     * @return 数据源
     */
    public static DataSource createH2FileDataSource(String name) {
        return createH2FileDataSource(".", name);
    }

    /**
     * 初始化本地文件數據源
     *
     * @param director 数据库路径
     * @param name     名称
     * @return 数据源
     */
    public static DataSource createH2FileDataSource(String director, String name) {
        String url = "jdbc:h2:file:" + director + "/" + (name.contains(".") ? name : (name + ".db") + ";AUTO_SERVER=TRUE;MODE=MySQLFILE_LOCK=SOCKET;MVCC=true");
        return createDataSource("org.h2.Driver", url, null, null, 100, null);
    }

    /**
     * 初始化本地文件數據源
     *
     * @param name 名称
     * @return 数据源
     */
    public static DataSource createLocalFileDataSource(String name) {
        return createLocalFileDataSource(".", name);
    }

    /**
     * 初始化本地文件數據源
     *
     * @param director 数据库路径
     * @param name     名称
     * @return 数据源
     */
    public static DataSource createLocalFileDataSource(String director, String name) {
        FileUtils.mkdir(new File(director));
        String url = "jdbc:sqlite:" + director + "/" + (name.contains(".") ? name : (name + ".db"));
        return createDataSource("org.sqlite.JDBC", url, null, null, 100, null);
    }

    /**
     * 初始化數據源
     *
     * @param supplier 回调
     * @return 数据源
     */
    public static DataSource createDataSource(Supplier<Connection> supplier) {
        return new DelegateDataSource(supplier);
    }

    /**
     * 初始化數據源
     *
     * @param jdbcUrl   url
     * @param username  账号
     * @param password  密码
     * @param maxActive 最大激活数量
     * @return 数据源
     */
    public static DataSource createDataSource(final String jdbcUrl, final String username, final String password, final int maxActive) {
        NetAddress netAddress = NetAddress.of(jdbcUrl);
        Dialect dialect = Dialect.create(netAddress.getProtocol());
        return createDataSource(dialect.driverClassName(), jdbcUrl, username, password, maxActive, null);
    }

    /**
     * 初始化數據源
     *
     * @param jdbcUrl   url
     * @param username  账号
     * @param password  密码
     * @param maxActive 最大激活数量
     * @return 数据源
     */
    public static DataSource createDataSource(final String jdbcUrl, final String username, final String password, final int maxActive, UrlExt urlExt) {
        NetAddress netAddress = NetAddress.of(jdbcUrl);
        Dialect dialect = Dialect.create(netAddress.getProtocol());
        return createDataSource(dialect.driverClassName(), jdbcUrl, username, password, maxActive, urlExt);
    }

    /**
     * 初始化數據源
     *
     * @param jdbcUrl  url
     * @param username 账号
     * @param password 密码
     * @return 数据源
     */
    public static DataSource createDataSource(final String jdbcUrl, final String username, final String password) {
        NetAddress netAddress = NetAddress.of(jdbcUrl);
        Dialect dialect = Dialect.create(netAddress.getProtocol());
        return createDataSource(dialect.driverClassName(), jdbcUrl, username, password);
    }

    /**
     * 初始化數據源
     *
     * @param jdbcUrl  url
     * @param username 账号
     * @param password 密码
     * @return 数据源
     */
    public static DataSource createDataSource(final String jdbcUrl, final String username, final String password, UrlExt urlExt) {
        NetAddress netAddress = NetAddress.of(jdbcUrl);
        Dialect dialect = Dialect.create(netAddress.getProtocol());
        return createDataSource(dialect.driverClassName(), jdbcUrl, username, password, urlExt);
    }

    /**
     * 初始化數據源
     *
     * @param driver   驱动
     * @param jdbcUrl  url
     * @param username 账号
     * @param password 密码
     * @return 数据源
     */
    public static DataSource createDataSource(final String driver, final String jdbcUrl, final String username, final String password) {
        return createDataSource(driver, jdbcUrl, username, password, 100, UrlExt.builder().build());
    }

    /**
     * 初始化數據源
     *
     * @param driver   驱动
     * @param jdbcUrl  url
     * @param username 账号
     * @param password 密码
     * @return 数据源
     */
    public static DataSource createDataSource(final String driver, final String jdbcUrl, final String username, final String password, UrlExt urlExt) {
        return createDataSource(driver, jdbcUrl, username, password, 100, urlExt);
    }

    /**
     * 初始化數據源
     *
     * @param driver    驱动
     * @param jdbcUrl   url
     * @param username  账号
     * @param password  密码
     * @param maxActive 最大激活数量
     * @return 数据源
     */
    public static DataSource createDataSource(final String driver, String jdbcUrl, final String username, final String password, final int maxActive, final UrlExt urlExt) {
        Map.Entry<Class<?>, Map<String, String>> entry = MapUtils.getFirst(dataSourceAndParam);
        Object forObject = ClassUtils.forObject(entry.getKey());
        MethodStation methodStation = MethodStation.of(forObject);
        Map<String, String> value = entry.getValue();
        methodStation.invoke(value.get("driver"), driver);
        if (null != urlExt) {
            jdbcUrl += "?" + Joiner.on("&").withKeyValueSeparator("=").join(BeanMap.create(urlExt));
        }
        methodStation.invoke(value.get("url"), jdbcUrl);
        methodStation.invoke(value.get("username"), username);
        methodStation.invoke(value.get("password"), password);
        methodStation.invoke(value.get("maxActive"), maxActive);

        if (forObject instanceof InitializingAware) {
            ((InitializingAware) forObject).afterPropertiesSet();
        }
        return (DataSource) forObject;
    }
}
