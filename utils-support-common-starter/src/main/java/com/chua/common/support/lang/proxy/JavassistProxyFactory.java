package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.List;

/**
 * javassist代理
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class JavassistProxyFactory<T> implements ProxyFactory<T> {

    public static final ProxyFactory INSTANCE = new JavassistProxyFactory();

    @Override
    @SneakyThrows
    public T proxy(Class<T> target, ClassLoader classLoader, MethodIntercept<T> intercept, ProxyPlugin[] plugins) {
        javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
        proxyFactory.setSuperclass(target);
        Class<?> aClass = proxyFactory.createClass(new JavassistMethodFilter(intercept, plugins));
        Object newInstance = aClass.newInstance();
        ProxyObject proxyObject  = (ProxyObject) newInstance;
        proxyObject.setHandler(new MethodHandler() {
            @Override
            public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                intercept.before(self, thisMethod, args, (T) self, plugins);
                try {
                    return intercept.invoke(self, thisMethod, args, (T) self, plugins);
                } finally {
                    intercept.after(self, thisMethod, args, (T) self, plugins);
                }
            }
        });
        return (T) newInstance;
    }


    final class JavassistMethodFilter implements MethodFilter {

        private final MethodIntercept<T> intercept;
        private final ProxyPlugin[] proxyPluginList;

        public JavassistMethodFilter(MethodIntercept<T> intercept, ProxyPlugin[] proxyPluginList) {
            this.intercept = intercept;
            this.proxyPluginList = proxyPluginList;
        }

        @Override
        public boolean isHandled(Method m) {
            return true;
        }
    }
}
