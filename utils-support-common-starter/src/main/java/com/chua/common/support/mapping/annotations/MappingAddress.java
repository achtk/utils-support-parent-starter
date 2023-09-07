package com.chua.common.support.mapping.annotations;

import java.lang.annotation.*;

/**
 * 实体映射
 * @author CH
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappingAddress {
    /**
     * 远程地址
     * @return 远程地址
     * @see com.chua.common.support.mapping.MappingConfig
     */
    String value() default "";

    /**
     * balance
     * @return balance
     */
    String balance() default "round";

    /**
     * 超时时间(s)
     * @return 超时时间
     */
    int readTimeout() default 30_000;

    /**
     * 超时时间(s)
     *
     * @return 超时时间
     */
    int connectTimeout() default 10_000;


    /**
     * 调用类型
     *
     * @return {@link String}
     */
    String invokeType() default "default";
}
