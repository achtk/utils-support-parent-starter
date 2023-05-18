package com.chua.common.support.task.retry;


import com.chua.common.support.function.FailureCallback;

import java.lang.annotation.*;
import java.util.function.Predicate;

/**
 * 重试
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Retry {
    /**
     * 重试次数
     *
     * @return 重试次数
     */
    int value() default 3;

    /**
     * 超时时间(s)
     *
     * @return 超时时间(s)
     */
    int timeout() default 1;

    /**
     * 每次等待时间(s)
     *
     * @return 每次等待时间(s)
     */
    int exponential() default -1;

    /**
     * 实现
     *
     * @return 实现
     */
    String type() default "guava";

    /**
     * 异常类型
     *
     * @return 异常类型
     */
    Class<? extends Predicate> ofResult() default Predicate.class;

    /**
     * 异常类型
     *
     * @return 异常类型
     */
    Class<? extends Exception> ofType() default Exception.class;

    /**
     * 重试类型
     *
     * @return 重试类型
     */
    RetryProvider.Type retryType() default RetryProvider.Type.EX;

    /**
     * 回调
     *
     * @return 回调
     */
    Class<? extends FailureCallback> callback() default FailureCallback.class;
}
