package com.chua.common.support.task.timeout;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.FailureCallback;
import com.chua.common.support.utils.ThreadUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 简单超时处理
 *
 * @author CH
 */
@Spi("simple")
public class DelegateTimeoutProvider implements TimeoutProvider {
    @Override
    public Object execute(Supplier<Object> supplier, long timeout, FailureCallback function) {
        ExecutorService executorService = ThreadUtils.newSingleThreadExecutor();
        Future<Object> future = executorService.submit(supplier::get);

        try {
            return future.get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            return function.apply(e);
        } finally {
            ThreadUtils.shutdownNow(executorService);
        }
    }
}
