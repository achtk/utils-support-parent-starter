package com.chua.common.support.protocol.server.annotations;

import java.lang.annotation.*;

/**
 * 字段
 *
 * @author CH
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    String value();

    String defaultValue() default "";
}
