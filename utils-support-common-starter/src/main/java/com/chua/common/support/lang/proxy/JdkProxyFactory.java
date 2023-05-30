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
public class JdkProxyFactory<T> implements ProxyFactory<T> {

    public static final ProxyFactory INSTANCE = new JdkProxyFactory();

    @Override
    public T proxy(Class<T> target, ClassLoader classLoader, MethodIntercept<T> intercept, ProxyPlugin[] plugins) {
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{target}, new JdkInvocationHandler(intercept, plugins));
    }


    public static class JdkInvocationHandler<T> implements InvocationHandler {

        final MethodIntercept<T> intercept;
        final ProxyPlugin[] proxyPluginList;

        public JdkInvocationHandler(MethodIntercept<T> intercept, ProxyPlugin[] proxyPluginList) {
            this.intercept = intercept;
            this.proxyPluginList = proxyPluginList;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            intercept.before(proxy, method, args, (T) proxy, proxyPluginList);
            try {
                return intercept.invoke(proxy, method, args, (T) proxy, proxyPluginList);
            } catch (Exception e) {
                throw e;
            }finally {
                intercept.after(proxy, method, args, (T) proxy, proxyPluginList);
            }
        }
    }
}
