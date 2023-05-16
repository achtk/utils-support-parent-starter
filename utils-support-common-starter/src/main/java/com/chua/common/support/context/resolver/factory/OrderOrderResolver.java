package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.annotations.Order;
import com.chua.common.support.context.resolver.OrderResolver;

/**
 * order
 *
 * @author CH
 */
public class OrderOrderResolver implements OrderResolver {
    @Override
    public int order(Class<?> type) {
        if (null == type) {
            return 0;
        }
        Order order = type.getDeclaredAnnotation(Order.class);
        return null != order ? order.value() : 0;
    }
}
