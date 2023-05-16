package com.chua.common.support.context.annotation;

import com.chua.common.support.context.enums.Scope;

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
public @interface ProxyScope {
    /**
     * 作用范围
     *
     * @return 作用范围
     */
    Scope value() default Scope.SINGLE;
}
