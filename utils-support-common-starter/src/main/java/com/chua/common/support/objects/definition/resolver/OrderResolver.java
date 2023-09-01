package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Spi;

/**
 * 优先级解析器
 * @author CH
 */
@Spi
public interface OrderResolver {
    /**
     * 优先级
     * @return 优先级
     */
    int order();

    /**
     * 默认代理
     */
    @Spi("default")
    public class DefaultOrderResolver implements OrderResolver {

        @Override
        public int order() {
            return 0;
        }
    }
}
