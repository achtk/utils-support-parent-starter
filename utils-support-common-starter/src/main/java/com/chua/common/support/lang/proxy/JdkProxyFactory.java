package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * jdk代理
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class JdkProxyFactory<T> extends AbstractProxyFactory<T> {

    public static final ProxyFactory INSTANCE = new JdkProxyFactory();

    public JdkProxyFactory(List<ProxyPlugin> proxyPluginList) {
        super(proxyPluginList);
    }

    public JdkProxyFactory(ProxyPlugin... proxyPluginList) {
        super(proxyPluginList);
    }

    @Override
    public T proxy(Class<T> target, ClassLoader classLoader, MethodIntercept<T> intercept) {
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{target}, new JdkInvocationHandler(intercept, proxyPluginList));
    }


    public static class JdkInvocationHandler<T> implements InvocationHandler {

        final MethodIntercept<T> intercept;
        final List<ProxyPlugin> proxyPluginList;

        public JdkInvocationHandler(MethodIntercept<T> intercept, List<ProxyPlugin> proxyPluginList) {
            this.intercept = intercept;
            this.proxyPluginList = proxyPluginList;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            intercept.before(proxy, method, args, (T) proxy, proxyPluginList);
            try {
                return intercept.invoke(proxy, method, args, (T) proxy, proxyPluginList);
            } finally {
                intercept.after(proxy, method, args, (T) proxy, proxyPluginList);
            }
        }
    }
}
