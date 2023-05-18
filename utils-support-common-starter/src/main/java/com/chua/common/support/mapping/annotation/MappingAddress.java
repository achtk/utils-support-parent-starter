package com.chua.common.support.mapping.annotation;

import java.lang.annotation.*;

/**
 * request
 * @author CH
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingAddress {
    /**
     * 地址
     * @return 地址
     */
    String[] value();

    /**
     * 超时时间
     * @return 超时时间
     */
    long timeout() default 30_000L;

    /**
     * balance
     * @return balance
     */
    String balance() default "round";
}
