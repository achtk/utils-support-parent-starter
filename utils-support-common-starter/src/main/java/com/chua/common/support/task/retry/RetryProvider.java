package com.chua.common.support.task.retry;


import com.chua.common.support.function.FailureCallback;

import java.util.function.Supplier;

/**
 * 重试
 *
 * @author CH
 */
public interface RetryProvider {

    /**
     * 超时处理
     *
     * @param supplier    生产
     * @param retry       重试次数
     * @param type        重试类型
     * @param timeout     每次等待时间
     * @param exponential 每次间隔
     * @param value       异常方式
     * @param function    回调
     * @return 结果
     * @throws Exception ex
     */
    Object execute(Supplier<Object> supplier,
                   int retry,
                   int timeout,
                   int exponential,
                   Type type,
                   Object value,
                   FailureCallback function);

    /**
     * 类型
     */
    public static enum Type {
        /**
         * 异常重试
         */
        EX,
    }
}
