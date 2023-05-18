package com.chua.common.support.task.retry;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.FailureCallback;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.ThreadUtils;

import java.util.concurrent.atomic.LongAdder;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 重试
 *
 * @author CH
 */
@Spi("simple")
public class DelegateRetryProvider implements RetryProvider {

    @Override
    @SuppressWarnings("ALL")
    public Object execute(Supplier<Object> supplier, int retry, int timeout, int exponential, Type type, Object value, FailureCallback function) {
        if (retry < 1) {
            return supplier.get();
        }

        LongAdder longAdder = new LongAdder();
        while (longAdder.intValue() < retry) {
            if (type == Type.EX) {
                try {
                    return supplier.get();
                } catch (Exception e) {
                    if ((Exception.class.isAssignableFrom(ClassUtils.toType(value)))) {
                        longAdder.increment();
                        ThreadUtils.sleepSecondsQuietly(timeout + Math.max(exponential, 0));
                        continue;
                    }

                    return function.apply(e);
                }
            } else {
                try {
                    Object o = supplier.get();
                    if (ObjectUtils.withAssignableFrom(value, Predicate.class).test(o)) {
                        longAdder.increment();
                        ThreadUtils.sleepSecondsQuietly(timeout + Math.max(exponential, 0));
                        continue;
                    }
                    return o;
                } catch (Exception e) {
                    return function.apply(e);
                }
            }
        }

        return null;
    }
}
