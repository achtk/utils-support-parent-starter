package com.chua.common.support.lang.depends;

import java.lang.annotation.*;

/**
 * 依赖
 * @author CH
 */
@Documented
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface DependencyExcludeCollect {
    DependencyExclude[] value();
}
