package com.chua.common.support.protocol.client;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.pool.BoundBlockingPool;
import com.chua.common.support.lang.pool.ObjectFactory;
import com.chua.common.support.lang.pool.Pool;
import com.chua.common.support.lang.pool.PoolConfig;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.router.Router;
import com.chua.common.support.utils.ClassUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * 客户端
 *
 * @author CH
 */
public interface Client<T> extends InitializingAware, AutoCloseable {
    /**
     * 获取客户端
     *
     * @return 客户端
     */
    T getClient();

    /**
     * 获取客户端池
     *
     * @param poolConfig 配置
     * @return 客户端
     */
    @SuppressWarnings("ALL")
    default Pool<T> getClientPool(PoolConfig<T> poolConfig) {
        Class<?> argument = (Class<?>) ClassUtils.getActualTypeArguments(this.getClass())[0];
        BoundBlockingPool<T> pool = null;
        AtomicReference<BoundBlockingPool<T>> reference = new AtomicReference<>();
        try {
            return (pool = new BoundBlockingPool<>(() -> {
                T client = getClient();
                Class<?> aClass = client.getClass();
                T proxyClient = (T) ProxyUtils.newProxy(argument, new DelegateMethodIntercept(aClass, new Function<ProxyMethod, Object>() {
                    @Override
                    public Object apply(ProxyMethod proxyMethod) {
                        if (proxyMethod.is("close")) {
                            reference.get().releaseObject((T) proxyMethod.getObj());
                            return null;
                        }
                        return proxyMethod.getValue(client);
                    }
                }));

                return proxyClient;
            }, poolConfig, this::closeClient));
        } finally {
            reference.set(pool);
        }
    }

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


    /**
     * 获取客户端
     *
     * @param client 客户端
     */
    void closeClient(T client);
}
