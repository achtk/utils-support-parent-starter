package com.chua.common.support.database.annotation;

import java.lang.annotation.*;

/**
 * 主键
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Id {
    /**
     * 策略
     *
     * @return 策略
     */
    String strategy() default "increment";
}
