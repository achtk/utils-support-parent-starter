package com.chua.common.support.lang.proxy;

import com.chua.common.support.describe.Bench;
import com.chua.common.support.describe.Marker;
import com.chua.common.support.describe.describe.MethodDescribe;
import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.Method;

/**
 * 方法拦截器
 *
 * @author CH
 */
public class BridgingMethodIntercept<T> extends DelegateMethodIntercept<T> {

    public BridgingMethodIntercept(String type, Object bridging) {
        super((Class<T>) ClassUtils.forName(type), it -> {
            if (null == bridging) {
                return null;
            }

            Method method = it.getMethod();
            method.setAccessible(true);
            try {
                return method.invoke(bridging, it.getArgs());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public BridgingMethodIntercept(Class<T> type, Object bridging) {
        super(type, it -> {
            if (null == bridging) {
                return null;
            }

            Marker marker = Marker.of(bridging);
            Bench bench = marker.createBench(MethodDescribe.builder().method(it.getMethod()).build());
            Object value = bench.execute(it.getArgs()).getValue();
            Class<?> aClass = it.getMethod().getReturnType();
            if (null == value || aClass.isAssignableFrom(value.getClass())) {
                return value;
            }

            if (aClass.isInterface()) {
                return ProxyUtils.newProxy(aClass, new BridgingMethodIntercept(aClass, value));
            }

            return value;
        });
    }

}
