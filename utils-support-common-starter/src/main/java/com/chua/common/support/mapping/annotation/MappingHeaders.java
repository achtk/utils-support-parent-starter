package com.chua.common.support.mapping.annotation;

import java.lang.annotation.*;

/**
 * http消息头
 *
 * @author CH
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappingHeaders {
    /**
     * 消息头
     *
     * @return 消息头
     */
    MappingHeader[] value() default {};

}
