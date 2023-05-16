package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.Preprocess;
import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;
import lombok.AllArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * 方法拦截器
 *
 * @author CH
 */
@AllArgsConstructor
public class VoidMethodIntercept<T> implements MethodIntercept<T> {


    @Override
    public Object invoke(Object obj, Method method, Object[] args, T proxy, List<ProxyPlugin> proxyPluginList) throws Throwable {
        return null;
    }
}
