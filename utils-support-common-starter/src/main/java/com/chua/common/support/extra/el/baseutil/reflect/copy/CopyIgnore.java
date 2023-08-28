package com.chua.common.support.extra.el.baseutil.reflect.copy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * 基础类
 * @author CH
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CopyIgnore
{
    Class<?> from() default Object.class;

    Class<?> to() default Object.class;

    @Target({METHOD, FIELD})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        CopyIgnore[] value();
    }
}
