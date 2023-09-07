package com.chua.common.support.lang.proxy.plugin.processor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.objects.definition.element.MethodDescribe;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.task.limit.Limit;
import com.chua.common.support.task.limit.LimiterProvider;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author CH
 */
@Spi(value = "limit", order = 999)
public class LimitMethodAnnotationPostProcessor implements MethodAnnotationPostProcessor<Limit> {


    @Override
    @SuppressWarnings("ALL")
    public Object execute(MethodDescribe describe, Object entity, Object[] args) {
        Limit limit = describe.getAnnotation(Limit.class);
        String type = limit.type();
        double value = limit.value();
        String key = limit.key();
        LimiterProvider limiterProvider = ServiceProvider.of(LimiterProvider.class).getExtension(type);
        LimiterProvider limiterProvider1 = limiterProvider.newLimiter(key, value);
        try {
            if(limiterProvider1.tryAcquire(key)) {
                return true;
            }
            throw new RuntimeException("系统繁忙");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getKey(Object entity, Object[] args) {
        return "" + entity.hashCode() + Arrays.hashCode(args);
    }

    @Override
    public boolean hasAnnotation(Method method) {
        return method.isAnnotationPresent(Limit.class);
    }


}
