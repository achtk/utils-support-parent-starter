package com.chua.common.support.annotations;

import java.lang.annotation.*;

/**
 * 注解别名
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Alias {
    /**
     * 字段
     * @return 字段
     */
    String value();
}
