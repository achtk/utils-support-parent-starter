package com.chua.common.support.lang.proxy;

import com.chua.common.support.utils.ClassUtils;
import org.slf4j.Marker;

import java.lang.reflect.Method;

/**
 * 方法拦截器
 *
 * @author CH
 */
public class BridgingMethodIntercept<T> extends DelegateMethodIntercept<T> {

    public BridgingMethodIntercept(String type, Object bridging) {
        super(ClassUtils.forName(type), it -> {
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

            Method method = it.getMethod();
            ClassUtils.setAccessible(method);
            try {
                return ClassUtils.invokeMethod(method, bridging, it.getArgs());
            } catch (Exception e) {
                Method method1 = ClassUtils.findMethod(bridging.getClass(), method.getName(), ClassUtils.toType(it.getArgs()));
                ClassUtils.setAccessible(method1);
                return ClassUtils.invokeMethod(method1, bridging, it.getArgs());
            }

        });
    }

}
