package com.chua.common.support.extra.el.baseutil.reflect.copy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注解在拷贝的源头类上。代表着拷贝到目的类的哪一个属性
 *
 * @author linbin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CopyTo
{
    String name();

    Class<?> to();

    @Target({METHOD, FIELD})
    @Retention(RUNTIME)
    @Documented
    @interface List
    {
        CopyTo[] value();
    }
}
