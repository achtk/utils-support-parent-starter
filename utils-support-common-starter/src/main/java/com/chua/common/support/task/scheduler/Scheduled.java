package com.chua.common.support.task.scheduler;

import java.lang.annotation.*;

/**
 * 调度
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/23
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scheduled {

    /**
     * cron
     *
     * @return cron
     */
    String value() default "";

    /**
     * 名称
     *
     * @return 名称
     */
    String name() default "";

    /**
     * 优先级
     *
     * @return 优先级
     */
    int order() default 0;

    /**
     * 策略
     *
     * @return 策略
     */
    String strategy() default "";
}
