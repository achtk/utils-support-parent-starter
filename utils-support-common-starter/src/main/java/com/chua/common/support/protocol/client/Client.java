package com.chua.common.support.protocol.client;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.router.Router;

/**
 * 客户端
 *
 * @author CH
 */
public interface Client<T> extends InitializingAware, AutoCloseable {
    /**
     * 获取客户端连接池
     *
     * @param clientType 客户端类型
     * @return 客户端
     */
    T getPool(Class<T> clientType);
    /**
     * 连接
     *
     * @param url     地址
     * @param timeout 超时时间
     */
    void connect(String url, long timeout);

    /**
     * 连接
     *
     * @param timeout 超时时间
     */
    default void connect(long timeout) {
        connect(null, timeout);
    }

    /**
     * 连接
     */
    default void connect() {
        connect(null, 10000);
    }

    /**
     * 连接
     *
     * @param url 地址
     */
    default void connect(String url) {
        connect(url, 10000);
    }

    /**
     * 操作
     *
     * @return 操作
     */
    Router getRouter();

    /**
     * 关闭连接
     */
    @Override
    void close();
}
