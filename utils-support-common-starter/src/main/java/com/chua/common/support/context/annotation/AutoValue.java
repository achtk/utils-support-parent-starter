package com.chua.common.support.context.annotation;

import java.lang.annotation.*;

/**
 * AutoInject
 *
 * @author CH
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoValue {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @return the suggested component name, if any (or empty String otherwise)
     */
    String value() default "";


    String defaultValue() default "";

}
