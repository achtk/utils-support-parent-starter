package com.chua.common.support.database;


import com.chua.common.support.database.factory.DataSourceFactory;
import com.chua.common.support.database.factory.SimpleDataSourceFactory;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.inquirer.SubstanceInquirer;
import com.chua.common.support.spi.ServiceProvider;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库操作
 *
 * @author CH
 */
public class Database {

    private static final Map<Class<? extends DataSource>, DataSourceFactory> FACTORY_MAP = new ConcurrentHashMap<>();

    static {
        ServiceProvider<DataSourceFactory> provider = ServiceProvider.of(DataSourceFactory.class);
        provider.forEach((k, v) -> {
            Class<? extends DataSource> type = v.type();
            if (null == type) {
                return;
            }
            FACTORY_MAP.put(type, v);
        });
    }

    private String url;
    private String username;
    private transient String password;
    private String driver;
    private DataSource dataSource;
    private Class<? extends DataSource> dataSourceClass;
    private boolean isAutoCommit;

    /**
     * jdbc查询器
     *
     * @return jdbc查询器
     */
    public JdbcInquirer createJdbcInquirer() {
        return new JdbcInquirer(createDataSource(), isAutoCommit);
    }

    /**
     * 实体查询器
     *
     * @param type               类型
     * @param convertAllToFields 是否全部转化为字段
     * @param <T>                类型
     * @return 实体查询器
     */
    public <T> SubstanceInquirer<T> createEntityInquirer(Class<T> type, boolean convertAllToFields) {
        return new SubstanceInquirer<>(createDataSource(), isAutoCommit, type, convertAllToFields);
    }

    /**
     * datasource
     *
     * @return datasource
     */
    private DataSource createDataSource() {
        if (null != dataSource) {
            return dataSource;
        }

        if (null == dataSourceClass) {
            dataSourceClass = SimpleDataSourceFactory.SimpleDataSource.class;
        }

        DataSourceFactory dataSourceFactory = FACTORY_MAP.get(dataSourceClass);
        return dataSourceFactory.create(url, driver, username, password);
    }

    /**
     * 初始化构造器
     *
     * @return 构造器
     */
    public static DatabaseBuilder newBuilder() {
        return new DatabaseBuilder();
    }

    /**
     * 构造器
     *
     * @author CH
     */
    public static class DatabaseBuilder {
        private final Database database = new Database();

        /**
         * dataSource
         *
         * @param dataSource dataSource
         * @return this
         */
        public DatabaseBuilder url(DataSource dataSource) {
            database.dataSource = dataSource;
            return this;
        }

        /**
         * dataSourceClass
         *
         * @param dataSourceClass dataSourceClass
         * @return this
         */
        public DatabaseBuilder dataSourceClass(Class<? extends DataSource> dataSourceClass) {
            database.dataSourceClass = dataSourceClass;
            return this;
        }

        /**
         * url
         *
         * @param url url
         * @return this
         */
        public DatabaseBuilder url(String url) {
            database.url = url;
            return this;
        }

        /**
         * driver
         *
         * @param driver driver
         * @return this
         */
        public DatabaseBuilder driver(String driver) {
            database.driver = driver;
            return this;
        }

        /**
         * username
         *
         * @param username username
         * @return this
         */
        public DatabaseBuilder username(String username) {
            database.username = username;
            return this;
        }

        /**
         * password
         *
         * @param password password
         * @return this
         */
        public DatabaseBuilder password(String password) {
            database.password = password;
            return this;
        }

        /**
         * isAutoCommit
         *
         * @param isAutoCommit isAutoCommit
         * @return this
         */
        public DatabaseBuilder autoCommit(boolean isAutoCommit) {
            database.isAutoCommit = isAutoCommit;
            return this;
        }

        /**
         * 数据
         *
         * @return Database
         */
        public Database build() {
            return database;
        }

        /**
         * datasource
         *
         * @return datasource
         */
        public DatabaseBuilder datasource(DataSource dataSource) {
            database.dataSource = dataSource;
            return this;
        }
    }
}
