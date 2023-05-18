package com.chua.common.support.lang.proxy.plugin;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.function.FailureCallback;
import com.chua.common.support.reflection.describe.processor.impl.AbstractMethodAnnotationPostProcessor;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.task.retry.Retry;
import com.chua.common.support.task.retry.RetryProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

/**
 * 重试注解扫描
 *
 * @author CH
 */
@Extension("retry")
public class RetryMethodAnnotationPostProcessor extends AbstractMethodAnnotationPostProcessor<Retry> {

    @Override
    public Object execute(Object entity, Object[] args) {
        Retry retry = getAnnotationValue();
        if (retry.value() < 1) {
            return invoke(entity, args);
        }

        RetryProvider retryProvider = ServiceProvider.of(RetryProvider.class).getExtension(StringUtils.defaultString(retry.type(), "simple"));

        return retryProvider.execute(() -> invoke(entity, args),
                retry.value(),
                retry.timeout(),
                retry.exponential(),
                retry.retryType(),
                retry.retryType() == RetryProvider.Type.EX ? retry.ofType() : retry.ofResult(),
                throwable -> {
                    Class<? extends FailureCallback> callback = retry.callback();
                    if (callback.isInterface()) {
                        return null;
                    }
                    FailureCallback timeoutCallback = ClassUtils.forObject(callback, FailureCallback.class);
                    return timeoutCallback.apply(throwable);
                });
    }

    @Override
    public Class<Retry> getAnnotationType() {
        return Retry.class;
    }
}
