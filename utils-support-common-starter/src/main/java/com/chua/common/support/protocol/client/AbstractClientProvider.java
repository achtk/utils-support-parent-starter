package com.chua.common.support.protocol.client;

import com.chua.common.support.utils.ClassUtils;

/**
 * 客户端生产者
 *
 * @author CH
 */
public abstract class AbstractClientProvider<T> implements ClientProvider<T> {

    protected ClientOption option;

    public AbstractClientProvider(ClientOption option) {
        this.option = option;
    }

    @Override
    @SuppressWarnings("ALL")
    public Client<T> create() {
        return (Client<T>) ClassUtils.forObject(clientType(), option);
    }

    /**
     * 客户端类型
     * @return 类型
     */
    protected abstract Class<?> clientType();
}
