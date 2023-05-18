package com.chua.example;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author CH
 */
public class DataSourceUtils {


    private static final String MYSQL = "jdbc:mysql://localhost:3306/%s?characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_MYSQL = "jdbc:mysql://%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";

    /**
     * mysql url
     *
     * @param name 数据库
     * @return url
     */
    public static String localMysqlUrl(String name) {
        return "localhost:3306/" + name;
    }

    /**
     * 创建mysql客户端
     *
     * @param jdbcUrl url
     * @return 数据源
     */
    public static DataSource createDefaultMysqlDataSource(final String jdbcUrl) {
        return createMysqlDataSource(jdbcUrl, "root", "root");
    }

    /**
     * 创建mysql客户端
     *
     * @param jdbcUrl  url
     * @param username 账号
     * @param password 密码
     * @return 数据源
     */
    public static DataSource createMysqlDataSource(final String jdbcUrl, final String username, final String password) {
        // 使用默认连接池
        HikariDataSource result = new HikariDataSource();
        // 指定driver的类名，默认从jdbc url中自动探测
        //org.apache.calcite.jdbc.Driver
        result.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());
        // 设置数据库路径
        result.setJdbcUrl(String.format(DEFAULT_MYSQL, jdbcUrl));
        // 设置数据库用户名
        result.setUsername(username);
        // 设置数据密码
        result.setPassword(password);
        return result;
    }

    /**
     * 获取链接
     *
     * @param jdbcUrl  url
     * @param username 账号
     * @param password 密码
     * @return 链接
     * @throws SQLException ex
     */
    public static Connection getConnection(final String jdbcUrl, final String username, final String password) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * 获取链接
     *
     * @param jdbcUrl    url
     * @param properties 配置信息
     * @return 链接
     * @throws SQLException ex
     */
    public static Connection getConnection(final String jdbcUrl, final Properties properties) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, properties);
    }
}
