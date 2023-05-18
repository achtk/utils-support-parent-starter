package com.chua.common.support.database.factory;

import com.chua.common.support.utils.ClassUtils;
import lombok.SneakyThrows;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 简单DataSource
 *
 * @author CH
 */
public class SimpleDataSourceFactory implements DataSourceFactory {
    @Override
    public Class<? extends DataSource> type() {
        return SimpleDataSource.class;
    }

    @Override
    public DataSource create(String url, String driver, String username, String password) {
        ClassUtils.forObject(driver);
        return new SimpleDataSource(url, driver, username, password);
    }

    public static class DataSourceConnectionPool extends GenericObjectPool<Connection> {

        public DataSourceConnectionPool(PooledObjectFactory<Connection> factory) {
            super(factory);
        }

        public DataSourceConnectionPool(PooledObjectFactory<Connection> factory, GenericObjectPoolConfig<Connection> config) {
            super(factory, config);
        }

        public DataSourceConnectionPool(PooledObjectFactory<Connection> factory, GenericObjectPoolConfig<Connection> config, AbandonedConfig abandonedConfig) {
            super(factory, config, abandonedConfig);
        }
    }

    public static class DataSourceConnectionPoolFactory extends BasePooledObjectFactory<Connection> {
        private final String url;
        private final String driver;
        private final String username;
        private final String password;

        public DataSourceConnectionPoolFactory(String url, String driver, String username, String password) {
            this.url = url;
            this.driver = driver;
            this.username = username;
            this.password = password;
        }

        @Override
        public Connection create() throws Exception {
            return DriverManager.getConnection(url, username, password);
        }

        @Override
        public void destroyObject(PooledObject<Connection> p) throws Exception {
            super.destroyObject(p);
            p.getObject().close();
        }


        @Override
        public PooledObject<Connection> wrap(Connection obj) {
            return new DefaultPooledObject<>(obj);
        }

    }

    public static class SimpleDataSource implements DataSource, AutoCloseable {


        private final DataSourceConnectionPoolFactory dataSourceConnectionPoolFactory;
        private final DataSourceConnectionPool dataSourceConnectionPool;
        private int seconds;
        private PrintWriter out;

        public SimpleDataSource(String url, String driver, String username, String password) {
            this.dataSourceConnectionPoolFactory = new DataSourceConnectionPoolFactory(url, driver, username, password);
            this.dataSourceConnectionPool = new DataSourceConnectionPool(dataSourceConnectionPoolFactory);
        }

        @SneakyThrows
        @Override
        public Connection getConnection() throws SQLException {
            return dataSourceConnectionPool.borrowObject();
        }

        @SneakyThrows
        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return dataSourceConnectionPool.borrowObject();
        }

        @Override
        public <T> T unwrap(Class<T> tClass) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> aClass) throws SQLException {
            return false;
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return out;
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            this.out = out;
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            this.seconds = seconds;
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return seconds;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return Logger.getLogger("");
        }

        @Override
        public void close() throws Exception {
            dataSourceConnectionPool.close();
        }
    }

}
