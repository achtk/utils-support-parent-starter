package com.chua.common.support.lang.proxy.plugin;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.function.FailureCallback;
import com.chua.common.support.reflection.describe.processor.impl.AbstractMethodAnnotationPostProcessor;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.task.timeout.Timeout;
import com.chua.common.support.task.timeout.TimeoutProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

/**
 * 超时注解扫描
 *
 * @author CH
 */
@Extension("timeout")
public class TimeoutMethodAnnotationPostProcessor extends AbstractMethodAnnotationPostProcessor<Timeout> {

    @Override
    public Object execute(Object entity, Object[] args) {
        Timeout timeout = getAnnotationValue();
        if (-1 == timeout.value()) {
            return invoke(entity, args);
        }

        TimeoutProvider timeoutProvider = ServiceProvider.of(TimeoutProvider.class).getNewExtension(StringUtils.defaultString(timeout.type(), "simple"));
        return timeoutProvider.execute(() -> invoke(entity, args), timeout.value(), throwable -> {
            Class<? extends FailureCallback> callback = timeout.callback();
            if (callback.isInterface()) {
                return null;
            }
            FailureCallback timeoutCallback = ClassUtils.forObject(callback, FailureCallback.class);
            return timeoutCallback.apply(throwable);
        });
    }

    @Override
    public Class<Timeout> getAnnotationType() {
        return Timeout.class;
    }
}
