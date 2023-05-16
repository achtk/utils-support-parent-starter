package com.chua.common.support.lang.proxy;


import java.lang.reflect.Method;
import java.util.function.BiFunction;

/**
 * 代理
 *
 * @author CH
 */
public class ProxyUtils {
    /**
     * 代理
     *
     * @param target          类
     * @param methodIntercept 拦截器
     * @param <T>             类型
     * @return 代理类
     */
    public static <T> T newProxy(Class<T> target, MethodIntercept<T> methodIntercept) {
        return newProxy(target, target.getClassLoader(), methodIntercept);
    }

    /**
     * 代理
     *
     * @param target          类
     * @param classLoader     类加载器
     * @param methodIntercept 拦截器
     * @param <T>             类型
     * @return 代理类
     */
    @SuppressWarnings("ALL")
    public static <T> T newProxy(Class<T> target, ClassLoader classLoader, MethodIntercept<T> methodIntercept) {
        if (target.isInterface()) {
            return (T) JdkProxyFactory.INSTANCE.proxy(target, classLoader, methodIntercept);
        }

        return (T) JavassistProxyFactory.INSTANCE.proxy(target, classLoader, methodIntercept);
    }

    /**
     * 代理
     *
     * @param target      类
     * @param classLoader 类加载器
     * @param function    拦截器
     * @param <T>         类型
     * @return 代理类
     */
    public static <T> T proxy(Class<T> target, ClassLoader classLoader, BiFunction<Method, Object[], Object> function) {
        return newProxy(target, classLoader, (obj, method, args, proxy, proxyPluginList) -> function.apply(method, args));
    }

    /**
     * 代理
     *
     * @param target          类
     * @param classLoader     类加载器
     * @param methodIntercept 拦截器
     * @param <T>             类型
     * @return 代理类
     */
    public static <T> T proxy(Class<T> target, ClassLoader classLoader, MethodIntercept<T> methodIntercept) {
        return newProxy(target, classLoader, methodIntercept);
    }

}
