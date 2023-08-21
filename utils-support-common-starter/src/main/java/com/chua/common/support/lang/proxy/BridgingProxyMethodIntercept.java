package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;

import java.lang.reflect.Method;

/**
 * 方法拦截器
 *
 * @author CH
 */
public class BridgingProxyMethodIntercept<T> implements MethodIntercept<T> {

    private Class<T> type;
    private Object bridging;

    public BridgingProxyMethodIntercept(String type, Object bridging) {
        this((Class<T>) ClassUtils.forName(type), bridging);
    }

    public BridgingProxyMethodIntercept(Class<T> type, Object bridging) {
        this.type = type;
        this.bridging = bridging;
    }

    @Override
    public Object invoke(Object obj, Method method, Object[] args, T proxy, ProxyPlugin[] proxyPluginList) throws Throwable {
        if (MethodIntercept.isToString(method)) {
            return ObjectUtils.withNull(bridging, () -> "void", Object::toString);
        }

        if (MethodIntercept.isGetClass(method)) {
            return type;
        }

        if (MethodIntercept.isEquals(method)) {
            return ObjectUtils.withNull(bridging, () -> false, a -> a.equals(args[0]));
        }

        if (MethodIntercept.isHashCode(method)) {
            return ObjectUtils.withNull(bridging, type::hashCode, Object::hashCode);
        }

        if (null == bridging) {
            return null;
        }

        ClassUtils.setAccessible(method);
        try {
            Class<?> returnType = method.getReturnType();
            return ProxyUtils.proxy(returnType, returnType.getClassLoader(), new BridgingProxyMethodIntercept(returnType, ClassUtils.invokeMethod(method, bridging, args)));
        } catch (Exception e) {
            Method method1 = ClassUtils.findMethod(bridging.getClass(), method.getName(), ClassUtils.toType(args));
            ClassUtils.setAccessible(method1);
            return ClassUtils.invokeMethod(method1, bridging, args);
        }
    }
}
