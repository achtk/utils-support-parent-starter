package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.database.SqlModel;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.JdbcType;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.geo.Point;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_QUESTION;

/**
 * mysql
 *
 * @author CH
 */
@Spi({"mysql"})
public class MysqlDialect implements Dialect {

    public static final Map<Class<?>, JdbcType> JAVA_JDBC = new ConcurrentHashMap<>();
    public static final Map<JdbcType, Class<?>> JDBC_JAVA = new ConcurrentHashMap<>();

    static {
        JAVA_JDBC.put(String.class, JdbcType.VARCHAR);
        JAVA_JDBC.put(Long.class, JdbcType.INTEGER);
        JAVA_JDBC.put(Integer.class, JdbcType.INTEGER);
        JAVA_JDBC.put(Short.class, JdbcType.INTEGER);
        JAVA_JDBC.put(Byte.class, JdbcType.SMALLINT);
        JAVA_JDBC.put(Float.class, JdbcType.DECIMAL);
        JAVA_JDBC.put(Date.class, JdbcType.DATE);
        JAVA_JDBC.put(java.sql.Date.class, JdbcType.DATE);
        JAVA_JDBC.put(LocalDate.class, JdbcType.DATE);
        JAVA_JDBC.put(LocalDateTime.class, JdbcType.DATETIME);
        JAVA_JDBC.put(LocalTime.class, JdbcType.TIME);
        JAVA_JDBC.put(Point.class, JdbcType.POINT);

        for (Map.Entry<Class<?>, JdbcType> entry : JAVA_JDBC.entrySet()) {
            JDBC_JAVA.put(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public SqlModel formatPageSql(String originalSql, int offset, int limit) {
        StringBuilder sql = new StringBuilder(originalSql).append(" LIMIT ").append(SYMBOL_QUESTION);
        if (offset != 0L) {
            sql.append(SYMBOL_COMMA).append(SYMBOL_QUESTION);
            return new SqlModel(sql.toString(), offset, limit);
        }
        return new SqlModel(sql.toString(), limit);
    }

    @Override
    public String driverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String createCreateSql(Metadata<?> metadata) {
        StringBuilder sb = new StringBuilder();
        sb.append(CommonConstant.SYMBOL_CREATE_TABLE)
                .append(" `").append(metadata.getTable()).append("` (");

        for (Column column : metadata.getColumn()) {
            sb.append(createColumn(column));
            if (column.isPrimary()) {
                sb.append(" AUTO_INCREMENT ");
            }
            sb.append(",");
        }

        String keyProperty = metadata.getKeyProperty();
        if (StringUtils.isNotBlank(keyProperty)) {
            sb.append("KEY ").append(keyProperty).append(" (").append(keyProperty).append(")");
        } else {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append(" ) ENGINE=InnoDB DEFAULT CHARSET=").append(metadata.getUncode()).append(" COMMENT='").append(metadata.getTableComment()).append("'");
        return sb.toString();
    }

    @Override
    public Object getHibernateDialect() {
        return ClassUtils.forObject("org.hibernate.dialect.MySQLDialect");
    }

    @Override
    public String getProtocol() {
        return "Mysql";
    }

    @Override
    public JdbcType createJdbcType(Class<?> javaType) {
        return JAVA_JDBC.get(javaType);
    }

    @Override
    public Class<?> toJavaType(String name) {
        return JDBC_JAVA.get(JdbcType.valueOf(name));
    }

}
