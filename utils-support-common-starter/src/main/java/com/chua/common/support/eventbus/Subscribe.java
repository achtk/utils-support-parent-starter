package com.chua.common.support.eventbus;

import com.chua.common.support.annotations.Alias;
import com.chua.common.support.constant.Action;

import java.lang.annotation.*;

/**
 * 订阅
 *
 * @author CH
 * @version 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Subscribe {
    /**
     * 名称
     *
     * @return 名称
     */
    @Alias("name")
    String value() default "";

    /**
     * 名称
     *
     * @return 名称
     */
    @Alias("value")
    String name() default "";

    /**
     * 类型
     *
     * @return 类型
     */
    String typeName() default "DEFAULT";
    /**
     * 类型
     *
     * @return 类型
     */
    @Alias("typeName")
    EventbusType type() default EventbusType.DEFAULT;

    /**
     * 动作
     * @return 动作
     */
    Action action() default Action.NONE;
}
