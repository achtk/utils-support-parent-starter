package com.chua.common.support.function;

/**
 * 超时回调
 *
 * @author CH
 */
public interface FailureCallback {
    /**
     * 异常回调
     *
     * @param throwable the function argument
     * @return 回调结果
     */
    Object apply(Throwable throwable);
}
