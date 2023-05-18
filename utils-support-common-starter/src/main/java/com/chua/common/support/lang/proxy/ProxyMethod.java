package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;
import com.chua.common.support.utils.ClassUtils;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 代理方法
 *
 * @author CH
 */
@Data
@Builder
public class ProxyMethod {

    private Object obj;
    private Method method;
    private Object[] args;
    private Object proxy;

    private ProxyPlugin[] plugins;

    private boolean isReload;

    public boolean is(String methodName) {
        return this.method.getName().equals(methodName);
    }

    public <T> Object getValue(T client) {
        return ClassUtils.invokeMethod(method, client, args);
    }


    public <T> Object getValue() {
        return ClassUtils.invokeMethod(method, proxy, args);
    }

    public boolean hasReturnValue() {
        return method.getReturnType() != void.class && method.getReturnType() != Void.class;
    }

    public Object invoke(Object bean, Object[] args) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        try {
            return method.invoke(bean, args);
        } catch (Exception ignored) {
            if (isReload) {
                return null;
            }
            method = ClassUtils.findMethod(bean.getClass(), method.getName(), method.getParameterTypes());
            try {
                method.setAccessible(true);
                return method.invoke(bean, args);
            } finally {
                isReload = true;
            }
        }
    }
}
