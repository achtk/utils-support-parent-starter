package com.chua.datasource.support;

import com.google.common.collect.ImmutableMap;
import org.apache.calcite.sql.type.SqlTypeName;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * sql name
 *
 * @author CH
 * @since 2021-11-10
 */
public class SqlNameUtils {

    protected static final Map<Class<?>, SqlTypeName> CLASS_FAMILIES =
            ImmutableMap.<Class<?>, SqlTypeName>builder()
                    .put(String.class, SqlTypeName.VARCHAR)
                    .put(byte[].class, SqlTypeName.BINARY)
                    .put(boolean.class, SqlTypeName.BOOLEAN)
                    .put(Boolean.class, SqlTypeName.BOOLEAN)
                    .put(char.class, SqlTypeName.TINYINT)
                    .put(Character.class, SqlTypeName.TINYINT)
                    .put(short.class, SqlTypeName.TINYINT)
                    .put(Short.class, SqlTypeName.TINYINT)
                    .put(int.class, SqlTypeName.INTEGER)
                    .put(Integer.class, SqlTypeName.INTEGER)
                    .put(long.class, SqlTypeName.BIGINT)
                    .put(Long.class, SqlTypeName.BIGINT)
                    .put(Date.class, SqlTypeName.DATE)
                    .put(float.class, SqlTypeName.DECIMAL)
                    .put(Float.class, SqlTypeName.DECIMAL)
                    .put(double.class, SqlTypeName.DECIMAL)
                    .put(Double.class, SqlTypeName.DECIMAL)
                    .put(java.sql.Date.class, SqlTypeName.DATE)
                    .put(Time.class, SqlTypeName.TIME)
                    .put(Timestamp.class, SqlTypeName.TIMESTAMP)
                    .build();

    /**
     * 获取类型
     *
     * @param javaType java类型
     * @return sqltypename
     */
    public static SqlTypeName get(Class<?> javaType) {
        return CLASS_FAMILIES.getOrDefault(javaType, SqlTypeName.ANY);
    }

    /**
     * 获取类型
     *
     * @param javaType java类型
     * @return sqltypename
     */
    public static SqlTypeName get(String javaType) {
        SqlTypeName value = CLASS_FAMILIES.get(javaType);
        if (null != value) {
            return value;
        }

        if ("string".equals(javaType.toLowerCase())) {
            return SqlTypeName.VARBINARY;
        }

        return SqlTypeName.ANY;
    }
}
