package com.chua.common.support.mapping.annotation;

import java.lang.annotation.*;

/**
 * plugins
 * @author CH
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingPlugins {
    /**
     * 插件
     * @return 插件
     */
    String[] value();
}
