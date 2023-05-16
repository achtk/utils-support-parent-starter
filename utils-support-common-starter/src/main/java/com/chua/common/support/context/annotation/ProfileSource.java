package com.chua.common.support.context.annotation;

import java.lang.annotation.*;

/**
 * 配置
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ProfileSource {
    /**
     * Spi名称
     *
     * @return Spi名称
     */
    String[] value() default {};
}
