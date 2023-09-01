package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Spi;

/**
 * 优先级解析器
 *
 * @author CH
 */
@Spi
public interface NameResolver {
    /**
     * 优先级
     *
     * @return 优先级
     */
    String name();

    /**
     * 默认代理
     */
    @Spi("default")
    public class DefaultOrderResolver implements NameResolver {

        @Override
        public String name() {
            return null;
        }
    }
}
