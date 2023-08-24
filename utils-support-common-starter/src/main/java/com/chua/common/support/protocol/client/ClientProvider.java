package com.chua.common.support.protocol.client;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.net.NetAddress;
import com.chua.common.support.spi.ServiceProvider;

/**
 * 客户端生产者
 *
 * @author CH
 */
public interface ClientProvider<T> {
    /**
     * 创建客户端
     *
     * @return 客户端
     */
    Client<T> create();

    /**
     * 创建客户端提供者
     *
     * @param url 名称
     * @return 客户端提供者
     */
    @SuppressWarnings("ALL")
    static ClientProvider newProvider(String url) {
        NetAddress address = NetAddress.of(url);
        ClientOption clientOption = new ClientOption();

        BeanUtils.copyProperties(address.parametric(), clientOption);
        clientOption.add("url", address.getAddress());
        clientOption.ream().putAll(address.parametric());
        return newProvider(address.getProtocol(), clientOption);
    }

    /**
     * 创建客户端提供者
     *
     * @param name 名称
     * @return 客户端提供者
     */
    @SuppressWarnings("ALL")
    static ClientProvider newProvider(String name, ClientOption clientOption) {
        return (ClientProvider) newProvider(name, clientOption, Object.class);
    }

    /**
     * 创建客户端提供者
     *
     * @param name   名称
     * @param target 客户端类型
     * @return 客户端提供者
     */
    @SuppressWarnings("ALL")
    static ClientProvider newProvider(String name, ClientOption clientOption, Class target) {
        return (ClientProvider) ServiceProvider.of(ClientProvider.class).getNewExtension(name, clientOption);
    }
}
