package com.chua.common.support.router;

import java.lang.annotation.*;

/**
 * 字段
 *
 * @author CH
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    /**
     * 路由名称
     * @return 路由名称
     */
    String value() default "";

    /**
     * 优先级
     * @return 优先级
     */
    int order() default 0;
}
