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
     * 资源的key,唯一
     * 作用：不同的接口，不同的流量控制
     */
    String key() default "";

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
     * 得不到令牌的提示语
     */
    String msg() default "系统繁忙,请稍后再试.";
    /**
     * 回调
     *
     * @return 回调
     */
    Class<? extends FailureCallback> callback() default FailureCallback.class;
}
