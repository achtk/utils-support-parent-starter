package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.annotation.ProxyScope;
import com.chua.common.support.context.enums.Scope;
import com.chua.common.support.context.resolver.ScopeResolver;

/**
 * Scope
 *
 * @author CH
 */
public class ScopeScopeResolver implements ScopeResolver {
    @Override
    public Scope scope(Class<?> type) {
        if (null == type) {
            return Scope.SINGLE;
        }
        ProxyScope proxyScope = type.getDeclaredAnnotation(ProxyScope.class);
        return null != proxyScope ? proxyScope.value() : Scope.SINGLE;
    }
}
