package com.chua.common.support.database.annotation;

import java.lang.annotation.*;

/**
 * 字段
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(IndicesRepeat.class)
public @interface Indices {
    /**
     * 字段名称
     *
     * @return 字段名称
     */
    String value() default "";

    /**
     * 排序[ASC|DESC]
     *
     * @return 排序
     */
    String order() default "";

    /**
     * 索引类型["" | UNIQUE]
     *
     * @return 默认值
     */
    String indexType() default "";

    /**
     * 索引类型["" | UNIQUE]
     *
     * @return 默认值
     */
    String comment() default "";
}
