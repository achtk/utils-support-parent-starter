package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;
import lombok.AllArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * 方法拦截器
 *
 * @author CH
 */
@AllArgsConstructor
public class DelegateMethodIntercept<T> implements MethodIntercept<T> {

    private final Class<?> type;
    private final Function<ProxyMethod, Object> function;

    @Override
    public Object invoke(Object obj, Method method, Object[] args, T proxy, ProxyPlugin[] proxyPluginList) throws Throwable {
        if (MethodIntercept.isToString(method)) {
            return type.getTypeName();
        }

        if (MethodIntercept.isGetClass(method)) {
            return type;
        }

        if (MethodIntercept.isEquals(method)) {
            return false;
        }

        if (MethodIntercept.isHashCode(method)) {
            return type.hashCode();
        }

        if (null == function) {
            return null;
        }

        ProxyMethod proxyMethod = ProxyMethod.builder()
                .args(args)
                .method(method)
                .obj(obj)
                .plugins(proxyPluginList)
                .proxy(proxy).build();

        return function.apply(proxyMethod);
    }
}
