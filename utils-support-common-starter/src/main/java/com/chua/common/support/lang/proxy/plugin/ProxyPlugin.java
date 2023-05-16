package com.chua.common.support.lang.proxy.plugin;

import com.chua.common.support.lang.proxy.ProxyMethod;

/**
 * 代理插件
 *
 * @author CH
 */
public interface ProxyPlugin {
    /**
     * 插件
     *
     * @param proxyMethod 代理方法
     * @return 结果
     */
    Object execute(ProxyMethod proxyMethod);
}
