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
public @interface SpiDefault {
}
