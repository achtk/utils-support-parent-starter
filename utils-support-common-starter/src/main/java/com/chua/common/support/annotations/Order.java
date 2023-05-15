package com.chua.common.support.annotations;

import java.lang.annotation.*;

/**
 * 优先级
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Order {
    /**
     * 优先级
     *
     * @return 优先级
     */
    int value() default 0;
}
