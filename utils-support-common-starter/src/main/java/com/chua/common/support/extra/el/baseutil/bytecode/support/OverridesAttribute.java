package com.chua.common.support.extra.el.baseutil.bytecode.support;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface OverridesAttribute
{
    /**
     * 需要覆盖的注解
     *
     * @return
     */
    Class<? extends Annotation> annotation();

    /**
     * 需要覆盖的属性名称
     *
     * @return
     */
    String name();
}
