package com.chua.common.support.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 名称
 *
 * @author CH
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanProperty {
    /**
     * 名称
     *
     * @return 名称
     */
    String value();
}
