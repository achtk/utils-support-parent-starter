package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.ProxyMethodPlugin;
import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;
import com.chua.common.support.objects.definition.element.MethodDescribe;
import com.chua.common.support.spi.ServiceProvider;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * 方法拦截器
 *
 * @author CH
 */
public class DelegateMethodIntercept<T> implements MethodIntercept<T> {

    private final Class<?> type;
    private final Function<ProxyMethod, Object> function;

    private final boolean openPlugin;

    public DelegateMethodIntercept(Class<?> type, Function<ProxyMethod, Object> function) {
        this(type, function, true);
    }

    public DelegateMethodIntercept(Class<?> type, Function<ProxyMethod, Object> function, boolean openPlugin) {
        this.type = type;
        this.function = function;
        this.openPlugin = openPlugin;
    }

    @Override
    public Object invoke(Object obj, Method method, Object[] args, T proxy, ProxyPlugin[] proxyPluginList) throws Throwable {
        if (MethodIntercept.isToString(method)) {
            return type.getTypeName();
        }

        if (MethodIntercept.isGetClass(method)) {
            return type;
        }

        if (MethodIntercept.isEquals(method)) {
            return false;
        }

        if (MethodIntercept.isHashCode(method)) {
            return type.hashCode();
        }

        if (null == function) {
            return null;
        }

        ProxyMethod proxyMethod = ProxyMethod.builder()
                .args(args)
                .method(method)
                .obj(obj)
                .methodDescribe(new MethodDescribe(method))
                .plugins(proxyPluginList)
                .proxy(proxy).build();

        if(openPlugin) {
            List<ProxyMethodPlugin> proxyMethodPlugins = checkMethodPlugin(method, proxyMethod);
            if(!proxyMethodPlugins.isEmpty()) {
                return executeByPlugin(proxyMethodPlugins, proxyMethod);
            }
        }

        if(proxyMethod.isDefault()) {
            return proxyMethod.doDefault();
        }

        return function.apply(proxyMethod);
    }

    /**
     * 通过插件执行
     *
     * @param proxyMethodPlugins 代理方法插件
     * @param proxyMethod        代理方法
     * @return {@link Object}
     */
    private Object executeByPlugin(List<ProxyMethodPlugin> proxyMethodPlugins, ProxyMethod proxyMethod) {
        Object value = null;
        for (ProxyMethodPlugin proxyMethodPlugin : proxyMethodPlugins) {
            value = proxyMethodPlugin.execute(proxyMethod.getMethodDescribe(), proxyMethod.getProxy(), proxyMethod.getArgs());
        }

        return value;
    }

    /**
     * 检查方法插件
     *
     * @param method      方法
     * @param proxyMethod 代理方法
     * @return boolean
     */
    @SuppressWarnings("ALL")
    private List<ProxyMethodPlugin> checkMethodPlugin(Method method, ProxyMethod proxyMethod) {
        List<ProxyMethodPlugin> collect = ServiceProvider.of(ProxyMethodPlugin.class).collect();
        List<ProxyMethodPlugin> plugins = new LinkedList<>();
        for (ProxyMethodPlugin proxyMethodPlugin : collect) {
            if(proxyMethodPlugin.hasAnnotation(method)) {
                plugins.add(proxyMethodPlugin);
            }
        }

        return plugins;
    }
}
