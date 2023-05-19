package com.chua.common.support.eventbus;

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
    String value() default "";

    /**
     * 名称
     *
     * @return 名称
     */
    String name() default "";

    /**
     * 类型
     *
     * @return 类型
     */
    EventbusType type() default EventbusType.LOCAL;

}
