package com.chua.common.support.database.annotation;

import java.lang.annotation.*;

/**
 * 表
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {

    /**
     * 表名
     *
     * @return 表名
     */
    String value() default "";

    /**
     * 数据库
     *
     * @return 数据库
     */
    String schema() default "";

    /**
     * 数据库
     *
     * @return 数据库
     */
    String catalog() default "";

    /**
     * 描述
     *
     * @return 数据库
     */
    String comment() default "";

    /**
     * 定义
     *
     * @return 定义
     */
    String definition() default "";
}
