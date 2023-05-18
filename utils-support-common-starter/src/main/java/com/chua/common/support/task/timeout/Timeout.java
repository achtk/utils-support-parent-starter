package com.chua.common.support.task.timeout;

import com.chua.common.support.function.FailureCallback;

import java.lang.annotation.*;

/**
 * 超时
 *
 * @author CH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Timeout {
    /**
     * 超时时间(s)
     *
     * @return 超时时间(s)
     */
    int value() default -1;

    /**
     * 实现
     *
     * @return 实现
     */
    String type() default "simple";

    /**
     * 回调
     *
     * @return 回调
     */
    Class<? extends FailureCallback> callback() default FailureCallback.class;
}
