
package com.chua.common.support.database.sqldialect;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.collection.TypeHashMap;
import com.chua.common.support.constant.RegexConstant;
import com.chua.common.support.database.SqlModel;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.JdbcType;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.lang.exception.NotSupportedException;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 数据方言
 *
 * @author CH
 * @since 1.7.0
 */
public interface Dialect {
    static Dialect guessDialect(DataSource ds) {
        try (Connection connection = ds.getConnection()) {
            String driverName = connection.getMetaData().getDriverName();
            Map<String, Dialect> stringDialectMap = ServiceProvider.of(Dialect.class).list();
            for (Map.Entry<String, Dialect> entry : stringDialectMap.entrySet()) {
                if (entry.getValue().driverClassName().equals(driverName)) {
                    return entry.getValue();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * 生成分页查询语句
     *
     * @param originalSql 语句
     * @param offset      当前位置
     * @param limit       限制数量
     * @return 结果
     */
    SqlModel formatPageSql(String originalSql, int offset, int limit);

    /**
     * 驱动
     *
     * @return 驱动
     */
    String driverClassName();

    /**
     * 获取表的所有字段
     *
     * @param connection 连接
     * @param table      表
     * @return 所有字段
     */
    default List<Column> getAllColumn(Connection connection, String table) {
        List<Column> rs;
        try {
            connection.setAutoCommit(true);
            rs = new LinkedList<>();
            try (ResultSet resultSet = connection.getMetaData().getColumns(null, null, table, null)) {
                while (resultSet.next()) {
                    Column column = new Column();
                    column.setTableName(table);
                    column.setName(resultSet.getString("COLUMN_NAME"));
                    column.setLength(resultSet.getInt("COLUMN_SIZE"));
                    column.setJdbcType(JdbcType.valueOf(resultSet.getString("TYPE_NAME")));
                    column.setNullable(resultSet.getBoolean("NULLABLE"));

                    rs.add(column);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IoUtils.closeQuietly(connection);
        }

        return rs;
    }

    /**
     * 获取数据源
     *
     * @param protocol 协议
     * @return 数据源
     */
    static Dialect create(String protocol) {
        protocol = protocol.replace("jdbc:", "");
        String hibernatePackage = "org.hibernate.dialect";
        String type = hibernatePackage + "." + protocol + "dialect";
        if ("mysql".equalsIgnoreCase(protocol)) {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                Class<? extends Driver> aClass = driver.getClass();
                String typeName = aClass.getTypeName();
                if (typeName.contains(protocol)) {
                    String s = aClass.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
                    String s1 = RegexUtils.get(RegexConstant.NUMBERS.pattern(), s, 0);
                    if (NumberUtils.toInt(s1, 0) >= 8) {
                        type = hibernatePackage + "." + protocol + "8dialect";
                        break;
                    }
                }
            }
        }

        Object extension = ServiceProvider.of("org.hibernate.dialect.Dialect").getExtension(type);

        return ProxyUtils.newProxy(Dialect.class, new DelegateMethodIntercept<>(Dialect.class, new Function<ProxyMethod, Object>() {
            @Override
            public Object apply(ProxyMethod proxyMethod) {
                if (proxyMethod.is("dialect")) {
                    return extension.getClass().getTypeName();
                }

                if (proxyMethod.is("getHibernateDialect")) {
                    return extension;
                }

                Object value = proxyMethod.getValue(extension);
                if (null != value && proxyMethod.hasReturnValue()) {
                    return value;
                }

                return proxyMethod.getValue();
            }
        }));
    }

    /**
     * 获取数据源
     *
     * @param dataSource 数据库
     * @return 数据源
     */
    static Dialect create(DataSource dataSource) throws NotSupportedException {
        if (null != dataSource) {
            TypeHashMap typeHashMap = new TypeHashMap();
            typeHashMap.addProfile(BeanMap.of(dataSource));
            NetAddress netAddress = NetAddress.of(typeHashMap.getString("driver-url", "url", "jdbc-url"));
            String driverName = typeHashMap.getString("driver-class-name", "jdbc-driver", "driver");

            String protocol = netAddress.getProtocol();
            if (null != protocol) {
                return create(protocol);
            }

            if (null == driverName) {
                return null;
            }
            return ServiceProvider.of(Dialect.class).getExtension(driverName);
        }
        throw new NotSupportedException("数据方言不支持,请自主实现");
    }


    /**
     * alter sql
     *
     * @param table  表名
     * @param column 字段
     * @return sql
     */
    default String createAlterColumn(String table, Column column) {
        StringBuilder sb = new StringBuilder("ALTER TABLE `").append(table).append("`");
        sb.append(" ADD `").append(column.getName()).append("` ").append(column.getJdbcType().name());
        appColumnSuffix(column, sb);
        return sb.toString();
    }

    default String createColumn(Column column) {
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(column.getName()).append("`");
        sb.append(" ").append(column.getJdbcType().name()).append("");

        appColumnSuffix(column, sb);

        return sb.toString();
    }

    /**
     * 后缀添加
     *
     * @param column 字段
     * @param sb     sql
     */
    default void appColumnSuffix(Column column, StringBuilder sb) {
        if (column.getLength() > 0) {
            sb.append("(").append(column.getLength()).append(")");
        }

        if (!column.isNullable()) {
            sb.append(" NOT NULL ");
        }

        if (StringUtils.isNotBlank(column.getDefaultValue())) {
            sb.append(" DEFAULT ").append(column.getDefaultValue());
        }


        if (StringUtils.isNotBlank(column.getComment())) {
            sb.append(" COMMENT '").append(column.getComment()).append("'");
        }
    }

    /**
     * 建表语句
     *
     * @param metadata 媒体数据
     * @return sql
     */
    String createCreateSql(Metadata<?> metadata);

    /**
     * 删除语句
     *
     * @param metadata 媒体数据
     * @return sql
     */
    default String createDropSql(Metadata<?> metadata) {
        return "DROP TABLE `" + metadata.getTable() + "`";
    }

    /**
     * dialect
     *
     * @return dialect
     */
    Object getHibernateDialect();

    /**
     * 数据库类型
     *
     * @param javaType java类型
     * @return 数据库类型
     */
    JdbcType createJdbcType(Class<?> javaType);

    /**
     * java类型
     *
     * @param name 数据库
     * @return java类型
     */
    Class<?> toJavaType(String name);
}


