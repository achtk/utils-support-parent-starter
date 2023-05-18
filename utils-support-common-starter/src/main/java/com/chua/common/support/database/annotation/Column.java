package com.chua.common.support.database.annotation;

import com.chua.common.support.database.entity.JdbcType;

import java.lang.annotation.*;

/**
 * 字段
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Column {
    /**
     * 字段名称
     *
     * @return 字段名称
     */
    String value() default "";

    /**
     * 描述
     *
     * @return 描述
     */
    String comment() default "";

    /**
     * 默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 长度
     *
     * @return 长度
     */
    int length() default 0;

    /**
     * 数据库类型
     *
     * @return 数据库类型
     */
    JdbcType jdbcType() default JdbcType.NONE;
}
