package com.chua.common.support.file.xml;

/*
Public Domain.
*/

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Administrator
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD})
public @interface JSONPropertyName {
    /**
     * @return The name of the property as to be used in the JSON Object.
     */
    String value();
}
