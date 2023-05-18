package com.chua.common.support.mapping.annotation;

import com.chua.common.support.mapping.filter.MappingFilter;

import java.lang.annotation.*;

/**
 * 响应为json格式化处理
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappingResponse {
    /**
     * 表达式
     *
     * @return 表达式
     */
    String value();

    /**
     * json
     * @return json
     */
    boolean isJson() default true;

    /**
     * 过滤器
     * @return 过滤器
     */
    Class<? extends MappingFilter> filter() default MappingFilter.class;
    /**
     * 实际类型(用于处理List)
     *
     * @return 实际类型
     */
    Class<?> target() default Void.class;
}
