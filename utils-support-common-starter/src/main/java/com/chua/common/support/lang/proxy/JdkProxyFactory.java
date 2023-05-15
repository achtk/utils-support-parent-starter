package com.chua.common.support.lang.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk代理
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class JdkProxyFactory<T> implements ProxyFactory<T> {

    public static final ProxyFactory INSTANCE = new JdkProxyFactory();

    @Override
    public T proxy(Class<T> target, ClassLoader classLoader, MethodIntercept<T> intercept) {
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{target}, new JdkInvocationHandler(intercept));
    }


    public static class JdkInvocationHandler<T> implements InvocationHandler {

        final MethodIntercept<T> intercept;

        public JdkInvocationHandler(MethodIntercept<T> intercept) {
            this.intercept = intercept;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            intercept.before(proxy, method, args, (T) proxy);
            try {
                return intercept.invoke(proxy, method, args, (T) proxy);
            } finally {
                intercept.after(proxy, method, args, (T) proxy);
            }
        }
    }
}
