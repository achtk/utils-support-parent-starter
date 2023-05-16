package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.annotations.Order;
import com.chua.common.support.context.resolver.NamePair;
import com.chua.common.support.context.resolver.OrderResolver;

/**
 * order
 *
 * @author CH
 */
public class OrderOrderResolver implements OrderResolver {
    @Override
    public int resolve(NamePair namePair) {
        if (null == namePair) {
            return 0;
        }
        Order order = namePair.getType().getDeclaredAnnotation(Order.class);
        return null != order ? order.value() : 0;
    }
}
