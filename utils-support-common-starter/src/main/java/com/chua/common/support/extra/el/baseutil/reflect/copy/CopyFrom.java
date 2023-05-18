package com.chua.common.support.extra.el.baseutil.reflect.copy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注解在拷贝的目的类上，代表从该属性从源头类的哪一个属性拷贝
 *
 * @author linbin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CopyFrom
{
    String name();

    Class<?> from();

    @Target({METHOD, FIELD})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        CopyFrom[] value();
    }
}
