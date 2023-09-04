package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Order;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.strategy.resolver.NamePair;
import com.chua.common.support.utils.ClassUtils;

/**
 * 优先级解析器
 * @author CH
 */
@Spi
public interface OrderResolver {
    /**
     * 优先级
     *
     * @return 优先级
     */
    int order();


    /**
     * 解析
     *
     * @param pair 一对
     * @return int
     */
    int resolve(NamePair pair);

    /**
     * 默认代理
     */
    @Spi("default")
    public class DefaultOrderResolver implements OrderResolver {

        @Override
        public int order() {
            return 0;
        }

        @Override
        public int resolve(NamePair namePair) {
            if (null == namePair) {
                return 0;
            }
            Order order = ClassUtils.getDeclaredAnnotation(namePair.getType(), Order.class);
            return null != order ? order.value() : 0;
        }
    }
}
