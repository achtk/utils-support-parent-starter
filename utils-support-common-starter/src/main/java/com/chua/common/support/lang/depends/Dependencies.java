package com.chua.common.support.lang.depends;

import java.lang.annotation.*;

/**
 * 依赖
 * @author CH
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependencies {

    Dependency[] value();
}
