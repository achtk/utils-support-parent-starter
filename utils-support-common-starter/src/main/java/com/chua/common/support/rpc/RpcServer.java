package com.chua.common.support.rpc;

import com.chua.common.support.protocol.server.BeanServer;
import com.chua.common.support.spi.ServiceProvider;

import java.util.List;

/**
 * rpc服务器
 *
 * @author CH
 */
public interface RpcServer extends BeanServer {


    /**
     * 创建服务
     *
     * @param name               名称(实现方式)
     * @param rpcRegistryConfigs rpc注册表配置
     * @param rpcProtocolConfigs rpc协议配置
     * @param appName            应用程序名称
     * @return {@link RpcServer}
     */
    static RpcServer createService(String name, List<RpcRegistryConfig> rpcRegistryConfigs, List<RpcProtocolConfig> rpcProtocolConfigs, String appName) {
        return ServiceProvider.of(RpcServer.class).getNewExtension(name, rpcRegistryConfigs, rpcProtocolConfigs, appName);
    }

}
