package com.chua.common.support.annotations;

import java.lang.annotation.*;

/**
 * spi注解
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Spi {
    /**
     * Spi名称
     *
     * @return Spi名称
     */
    String[] value() default {};

    /**
     * 优先级
     *
     * @return 优先级
     */
    int order() default 0;
}
