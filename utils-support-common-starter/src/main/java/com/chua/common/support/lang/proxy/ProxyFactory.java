package com.chua.common.support.lang.proxy;

/**
 * 代理
 *
 * @author CH
 */
public interface ProxyFactory<T> {

    /**
     * 创建代理
     *
     * @param target      被代理对象
     * @param classLoader 类加载器
     * @param intercept   切面实现
     * @return 代理对象
     */
    T proxy(Class<T> target, ClassLoader classLoader, MethodIntercept<T> intercept);
}
