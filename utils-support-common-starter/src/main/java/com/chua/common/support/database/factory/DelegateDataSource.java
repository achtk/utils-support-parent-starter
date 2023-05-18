package com.chua.common.support.database.factory;

import com.chua.common.support.function.InitializingAware;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * 简单数据源
 *
 * @author CH
 */
public class DelegateDataSource extends GenericObjectPoolConfig<Connection> implements DataSource, InitializingAware {

    private PrintWriter printWriter = new PrintWriter(System.out);

    private final Logger logger = Logger.getLogger(DataSource.class.getName());

    private GenericObjectPool<Connection> pool;
    private int seconds;

    @Setter
    private String driverClass;
    @Setter
    private String jdbcUrl;
    @Setter
    private String username;
    @Setter
    private String password;


    private Supplier<Connection> supplier;
    public DelegateDataSource() {
        this(null);
    }
    public DelegateDataSource(Supplier<Connection> supplier) {
        this.supplier = supplier;
    }

    /**
     * 初始化数据库
     */
    private void initialDataSource() {
        PooledObjectFactory<Connection> factory = new BasePooledObjectFactory<Connection>() {
            @Override
            public Connection create() throws Exception {
                return supplier.get();
            }

            @Override
            public PooledObject<Connection> wrap(Connection obj) {
                return new DefaultPooledObject<>(obj);
            }
        };
        this.pool = new GenericObjectPool<>(factory, this);

    }

    @SneakyThrows
    @Override
    public Connection getConnection() throws SQLException {
        return pool.borrowObject();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return printWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.printWriter = out;
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
        return logger;
    }


    public void returnObject(Connection connection) {
        if (null == connection) {
            return;
        }

        pool.returnObject(connection);
    }

    @Override
    public void afterPropertiesSet() {
        if(null == supplier) {
            supplier = new Supplier<Connection>() {
                @Override
                public Connection get() {
                    try {
                        return DriverManager.getConnection(jdbcUrl, username, password);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
        initialDataSource();
    }
}
