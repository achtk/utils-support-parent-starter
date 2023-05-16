package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.JavassistUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * javassist代理
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class JavassistProxyFactory<T> extends AbstractProxyFactory<T> {

    public static final ProxyFactory INSTANCE = new JavassistProxyFactory();

    public JavassistProxyFactory(List<ProxyPlugin> proxyPluginList) {
        super(proxyPluginList);
    }

    public JavassistProxyFactory(ProxyPlugin... proxyPluginList) {
        super(proxyPluginList);
    }

    @Override
    @SneakyThrows
    public T proxy(Class<T> target, ClassLoader classLoader, MethodIntercept<T> intercept) {
        javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
        proxyFactory.setSuperclass(target);
        Class<?> aClass = proxyFactory.createClass(new JavassistMethodFilter(intercept, proxyPluginList));
        Object newInstance = aClass.newInstance();
        ProxyObject proxyObject  = (ProxyObject) newInstance;
        proxyObject.setHandler(new MethodHandler() {
            @Override
            public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                intercept.before(self, thisMethod, args, (T) self, proxyPluginList);
                try {
                    return intercept.invoke(self, thisMethod, args, (T) self, proxyPluginList);
                } finally {
                    intercept.after(self, thisMethod, args, (T) self, proxyPluginList);
                }
            }
        });
        return (T) newInstance;
    }


    final class JavassistMethodFilter implements MethodFilter {

        private final MethodIntercept<T> intercept;
        private final List<ProxyPlugin> proxyPluginList;

        public JavassistMethodFilter(MethodIntercept<T> intercept, List<ProxyPlugin> proxyPluginList) {
            this.intercept = intercept;
            this.proxyPluginList = proxyPluginList;
        }

        @Override
        public boolean isHandled(Method m) {
            return true;
        }
    }
}
