package com.chua.common.support.database.dialect;


import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.collection.TypeHashMap;
import com.chua.common.support.constant.RegexConstant;
import com.chua.common.support.database.actuator.DataSourceActuator;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.function.NameAware;
import com.chua.common.support.lang.exception.NotSupportedException;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.lang.profile.ProfileBuilder;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.NetAddress;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.RegexUtils;

import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

/**
 * 方言
 *
 * @author CH
 */
public interface Dialect  extends NameAware {
    /**
     * 把字段 dbField 转换为大写
     *
     * @param builder sql builder
     * @param dbField 数据库字段
     */
    default void toUpperCase(StringBuilder builder, String dbField) {
        builder.append("upper").append("(").append(dbField).append(")");
    }


    /**
     * 是否支持 ilike 语法
     * @return 是否支持 ilike 语法
     * @since v3.7.0
     */
    default boolean hasLike() {
        return false;
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
            NetAddress netAddress = NetAddress.of(typeHashMap.getString( "driver-url", "url", "jdbc-url"));
            String driverName = typeHashMap.getString("driver-class-name", "jdbc-driver", "driver");

            String protocol = netAddress.getProtocol();
            if(null != protocol) {
               return create(protocol);
            }

            if (null == driverName) {
                return new DelegateDialect();
            }
            return ServiceProvider.of(Dialect.class).getExtension(driverName);
        }
        throw new NotSupportedException("数据方言不支持,请自主实现");
    }

    /**
     * 协议
     *
     * @return 协议
     */
    String protocol();
    /**
     * 驱动
     *
     * @return 驱动
     */
    String driverClassName();

    /**
     * java转jdbc
     *
     * @param javaType java类型
     * @return jdbc
     */
    String toJdbcType(Class<?> javaType);

    /**
     * jdbc to  java
     *
     * @param jdbcType java类型
     * @return jdbc
     */
    Class<?> toJavaType(String jdbcType);

    /**
     * 获取默认长度
     *
     * @param jdbcType java类型
     * @return jdbc
     */
    int getDefaultLength(String jdbcType);

    /**
     * 名称
     * @return 名称
     */
    @Override
    default String[] named() {
        return new String[]{driverClassName(), protocol()};
    }

    /**
     * 数据库所有表
     *
     * @param dataSource 数据源
     * @return 结构
     */
    List<Metadata<?>> toMetaData(DataSource dataSource);

    /**
     * 创建表
     *
     * @param metadata   元数据
     * @param dataSource 数据源
     */
    void createTable(Metadata<?> metadata, DataSource dataSource);

    /**
     * 删除表
     *
     * @param metadata   元数据
     * @param dataSource 数据源
     */
    void dropTable(Metadata<?> metadata, DataSource dataSource);


    /**
     * 删除字段
     *
     * @param metadata   元数据
     * @param column     字段
     * @param dataSource 数据源
     */
    void dropColumn(Metadata<?> metadata, List<Column> column, DataSource dataSource);
    /**
     * 更新表
     *
     * @param dataSourceActuator a
     * @param metadata           元数据
     * @param dataSource         数据源
     */
    void updateTable(DataSourceActuator dataSourceActuator, Metadata<?> metadata, DataSource dataSource);

    /**
     * 添加字段
     *
     * @param metadata   元数据
     * @param column     字段
     * @param dataSource 数据源
     */
    void addColumn(Metadata<?> metadata, List<Column> column, DataSource dataSource);

    /**
     * 修改字段
     *
     * @param metadata   元数据
     * @param column     字段
     * @param dataSource 数据源
     */
    void modifyColumns(Metadata<?> metadata, List<Column> column, DataSource dataSource);

    /**
     * 分页语句
     *
     * @param querySql sql
     * @param offset   位置
     * @param limit    限制数量
     * @return sql
     */
    String getPageSql(String querySql, long offset, long limit);

    /**
     * 主键
     * @param metadata 元数据
     * @return 主键
     */
    String createPrimaryKey(Metadata<?> metadata);

    /**
     * 建表主键
     * @param stringBuffer sql
     * @param column 主键
     */
    void createPrimaryKey(StringBuffer stringBuffer, Column column);

    /**
     * 是否支持comment注解
     * @return 注解
     */
    default boolean supportComment() {
        return true;
    }

    /**
     * 方言
     *
     * @return 方言
     */
    String dialect();

    /**
     * dialect
     *
     * @return dialect
     */
    Object getHibernateDialect();
}