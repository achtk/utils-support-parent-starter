package com.chua.common.support.context.annotation;

import java.lang.annotation.*;

/**
 * 代理
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface ProxyFactory {
    /**
     * 是否开启代理
     *
     * @return 是否开启代理
     */
    boolean value() default true;
}
