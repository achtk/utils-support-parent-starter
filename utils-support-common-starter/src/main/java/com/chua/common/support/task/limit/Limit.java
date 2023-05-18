package com.chua.common.support.task.limit;

import com.chua.common.support.function.FailureCallback;

import java.lang.annotation.*;

/**
 * 限流
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Limit {
    /**
     * 限流次数
     *
     * @return 重试次数
     */
    double value() default 3;

    /**
     * 超时时间(s)
     *
     * @return 超时时间(s)
     */
    int timeout() default 1;

    /**
     * 实现
     *
     * @return 实现
     */
    String type() default "guava";

    /**
     * 回调
     *
     * @return 回调
     */
    Class<? extends FailureCallback> callback() default FailureCallback.class;
}
