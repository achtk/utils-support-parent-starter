package com.chua.common.support.context.resolver.factory;


import com.chua.common.support.context.annotation.ProxyFactory;
import com.chua.common.support.context.resolver.ProxyResolver;

/**
 * 代理
 *
 * @author CH
 */
public class ProxyFactoryProxyResolver implements ProxyResolver {
    @Override
    public boolean isProxy(Class<?> type) {
        ProxyFactory proxyFactory = type.getDeclaredAnnotation(ProxyFactory.class);
        return null == proxyFactory || proxyFactory.value();
    }
}
