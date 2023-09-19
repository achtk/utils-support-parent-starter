package com.chua.common.support.rpc;

/**
 * rpc客户端
 *
 * @author CH
 */
public interface RpcClient extends AutoCloseable{

    /**
     * 收到
     *
     * @param targetType 目标类型
     * @return {@link T}
     */
    <T>T get(Class<T> targetType);
}
