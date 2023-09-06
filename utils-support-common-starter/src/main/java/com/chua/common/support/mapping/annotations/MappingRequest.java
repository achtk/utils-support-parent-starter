package com.chua.common.support.mapping.annotations;

import java.lang.annotation.*;

/**
 * request
 * @author CH
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingRequest {
    /**
     * 地址
     * @return 地址
     */
    String value();

    /**
     * 超时时间(s)
     * @return 超时时间
     */
    int readTimeout() default 30_000;

    /**
     * 超时时间(s)
     * @return 超时时间
     */
    int connectTimeout() default 10_000;

    /**
     * 获取指定位置的响应
     * @return  获取指定位置的响应
     */
    String jsonPath() default "";

    /**
     * 响应类型（用于处理List）
     * @return 响应类型
     */
    Class<?> returnType() default Void.class;

}
