package com.chua.guava.support.retry;


import com.chua.common.support.task.retry.RetryProvider;

import java.util.function.Supplier;

/**
 * 重试
 *
 * @author CH
 */
public interface ExceptionRetryHandler extends RetryHandler {

    /**
     * 重试处理
     *
     * @param supplier    生产
     * @param retry       重试次数
     * @param timeout     每次等待时间
     * @param exponential 每次间隔
     * @param e           异常类型
     * @return 结果
     * @throws Exception ex
     */
    Object execute(Supplier<Object> supplier,
                   int retry,
                   int timeout,
                   int exponential,
                   Class<? extends Exception> e) throws Exception;

    /**
     * 重试处理
     *
     * @param supplier    生产
     * @param retry       重试次数
     * @param type        重试类型
     * @param timeout     每次等待时间
     * @param exponential 每次间隔
     * @return 结果
     * @throws Exception ex
     */
    default Object execute(Supplier<Object> supplier, int retry, int timeout,
                           int exponential, RetryProvider.Type type) throws Exception {
        return execute(supplier, retry, timeout, exponential, Exception.class);
    }


}
