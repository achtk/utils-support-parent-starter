package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Spi;

/**
 * 代理解析器
 * @author CH
 */
@Spi("default")
public interface ProxyResolver {
    /**
     * 是否代理
     * @return 是否代理
     */
    boolean isProxy();

    /**
     * 默认代理
     */
    @Spi("default")
    public class DefaultProxyResolver implements ProxyResolver{

        @Override
        public boolean isProxy() {
            return true;
        }
    }
}
