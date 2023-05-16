package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.annotations.Order;
import com.chua.common.support.context.resolver.NamePair;
import com.chua.common.support.context.resolver.OrderResolver;
import com.chua.common.support.utils.ClassUtils;

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
        Order order = ClassUtils.getDeclaredAnnotation(namePair.getType(), Order.class);
        return null != order ? order.value() : 0;
    }
}
