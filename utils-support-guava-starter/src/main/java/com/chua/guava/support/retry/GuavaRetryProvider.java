package com.chua.guava.support.retry;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.FailureCallback;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.task.retry.RetryProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 重试
 *
 * @author CH
 */
@Spi("guava")
public class GuavaRetryProvider implements RetryProvider {

    @Override
    public Object execute(Supplier<Object> supplier, int retry, int timeout, int exponential, Type type, Object value, FailureCallback failureCallback) {
        RetryHandler retryHandler = ServiceProvider.of(RetryHandler.class).getExtension(type.name());
        try {
            if (null == retryHandler) {
                return supplier.get();
            }

            if (ClassUtils.isAssignableFrom(retryHandler, ExceptionRetryHandler.class)) {
                return ObjectUtils.withAssignableFrom(retryHandler, ExceptionRetryHandler.class)
                        .execute(supplier, retry, timeout, exponential, (Class<? extends Exception>) value);
            }

            return ObjectUtils.withAssignableFrom(retryHandler, ResultRetryHandler.class)
                    .execute(supplier, retry, timeout, exponential, (Predicate<Object>) value);
        } catch (Exception e) {
            return failureCallback.apply(e);
        }
    }
}
