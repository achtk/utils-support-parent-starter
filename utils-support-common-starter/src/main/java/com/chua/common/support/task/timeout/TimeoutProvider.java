package com.chua.common.support.task.timeout;

import com.chua.common.support.function.FailureCallback;

import java.util.function.Supplier;

/**
 * 超时
 *
 * @author CH
 */
public interface TimeoutProvider {
    /**
     * 超时处理
     *
     * @param supplier 生产
     * @param timeout  超时时间
     * @param function 回调
     * @return 结果
     */
    Object execute(Supplier<Object> supplier, long timeout, FailureCallback function);
}
