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
public @interface IndicesRepeat {
    Indices[] value() default {};
}
