package com.chua.common.support.net.proxy;

/**
 * 代理通道
 *
 * @author CH
 * @since 2023/09/13
 */
public interface HttpProxyChannel extends ProxyChannel {
    /**
     * 代理
     *
     * @param req 绿色
     */
    void proxy(Object req);
}
