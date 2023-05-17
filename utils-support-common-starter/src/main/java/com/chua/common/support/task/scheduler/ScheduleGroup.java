package com.chua.common.support.task.scheduler;

import java.lang.annotation.*;

/**
 * 调度
 *
 * @author CH
 * @version 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScheduleGroup {

    /**
     * 分组名称
     *
     * @return cron
     */
    String value() default "default";

    /**
     * 实现方式
     *
     * @return 实现方式
     */
    String type() default "quartz";

    /**
     * 核心数
     *
     * @return 核心数
     */
    int core() default 1;
}
