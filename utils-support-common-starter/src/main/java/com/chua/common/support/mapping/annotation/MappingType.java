package com.chua.common.support.mapping.annotation;

import java.lang.annotation.*;

/**
 * 实现
 * @author CH
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingType {
    /**
     * 地址
     * @return 地址
     */
    String value();
}
