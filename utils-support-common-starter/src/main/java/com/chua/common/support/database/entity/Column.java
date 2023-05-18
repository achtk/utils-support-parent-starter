package com.chua.common.support.database.entity;

import com.chua.common.support.bean.BeanProperty;
import com.chua.common.support.database.dialect.Dialect;
import com.chua.common.support.database.jdbc.Type;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * 字段
 *
 * @author CH
 */
@Data
public class Column {
    /**
     * 是否是主键
     */
    private Primary primary;
    private Class<?> javaType;

    private String tableName;
    /**
     * 名称
     */
    @BeanProperty("value")
    private String name;
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 是否唯一
     */
    private boolean unique;
    /**
     * 是否可为空
     */
    private boolean nullable = true;
    /**
     * 是否可插入
     */
    private boolean insertable = true;
    /**
     * 是否可保存
     */
    private boolean updatable = true;
    /**
     * ddl
     */
    private String columnDefinition;
    /**
     * 长度
     */
    private int length;
    /**
     * 精度
     */
    private int precision;
    /**
     * 标度
     */
    private int scale;
    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 注释
     */
    @BeanProperty("COLUMN_COMMENT")
    private String comment;
    /**
     * 特殊指定数据库类型
     */
    @BeanProperty("DATA_TYPE")
    private JdbcType jdbcType;

    public Column() {
    }

    public Column(Field field) {
        setFieldName(field.getName());
        setJavaType(field.getType());

    }

    public int getLength() {
        return length == 0 ? (javaType == String.class ? 255 : 11) : length;
    }

    public boolean isPrimary() {
        return null != primary;
    }

    public String getJdbcType(Dialect dialect) {
        if (null != jdbcType && JdbcType.NONE != jdbcType) {
            return jdbcType.name();
        }

        return Type.valueTypeOf(javaType).name();
    }


    public Class<?> getJavaType(Dialect dialect) {
        return dialect.toJavaType(jdbcType.name());
    }
}
