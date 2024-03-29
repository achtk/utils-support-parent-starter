package com.chua.common.support.database.entity;

import com.chua.common.support.bean.BeanProperty;
import com.chua.common.support.database.enums.FieldStrategy;
import com.chua.common.support.database.sqldialect.Dialect;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;

/**
 * 字段
 *
 * @author CH
 */
@Data
@Accessors(chain = true)
public class Column {
    /**
     * 是否是主键
     */
    private Primary primary;
    private Class<?> javaType;

    private String tableName;

    /**
     * 字段验证策略之 where
     * @since added v_3.1.2 @2019-5-7
     */
    private FieldStrategy whereStrategy;
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
     * 字段是否存在
     */
    private boolean exist = true;
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

        return dialect.createJdbcType(javaType).name();
    }

    public void setExist(boolean exist) {
        if(!this.exist) {
            return;
        }
        this.exist = exist;
    }

    public Class<?> getJavaType(Dialect dialect) {
        return dialect.toJavaType(jdbcType.name());
    }
}
