package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.resolver.OrderResolver;

/**
 * order
 *
 * @author CH
 */
public class SpringOrderResolver implements OrderResolver {
    @Override
    public int order(Class<?> type) {
        if (null == type) {
            return 0;
        }
        org.springframework.core.annotation.Order order = type.getDeclaredAnnotation(org.springframework.core.annotation.Order.class);
        return null != order ? order.value() : 0;
    }
}
