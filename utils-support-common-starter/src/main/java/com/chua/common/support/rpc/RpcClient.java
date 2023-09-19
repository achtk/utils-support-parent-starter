package com.chua.common.support.rpc;

import com.chua.common.support.spi.ServiceProvider;

import java.util.List;

/**
 * rpc客户端
 *
 * @author CH
 */
public interface RpcClient extends AutoCloseable{

    /**
     * 创建客户端
     *
     * @param name               名称（实现方式）
     * @param rpcRegistryConfigs rpc注册表配置
     * @param appName            应用程序名称
     * @return {@link RpcClient}
     */
    static RpcClient createClient(String name, List<RpcRegistryConfig> rpcRegistryConfigs, String appName) {
        return ServiceProvider.of(RpcClient.class).getNewExtension(name, rpcRegistryConfigs, appName);
    }
    /**
     * 收到
     *
     * @param targetType 目标类型
     * @return {@link T}
     */
    <T>T get(Class<T> targetType);

}
