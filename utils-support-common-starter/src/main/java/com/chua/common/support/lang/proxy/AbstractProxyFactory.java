package com.chua.common.support.lang.proxy;

import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * 代理
 *
 * @author CH
 */
public abstract class AbstractProxyFactory<T> implements ProxyFactory<T>{

    protected List<ProxyPlugin> proxyPluginList;

    public AbstractProxyFactory(List<ProxyPlugin> proxyPluginList) {
        this.proxyPluginList = proxyPluginList;
    }

    public AbstractProxyFactory(ProxyPlugin... proxyPluginList) {
        this(Arrays.asList(proxyPluginList));
    }

}
