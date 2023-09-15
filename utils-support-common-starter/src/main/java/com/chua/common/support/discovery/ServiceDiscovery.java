package com.chua.common.support.discovery;


import com.chua.common.support.protocol.server.Server;

/**
 * 服务发现
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
public interface ServiceDiscovery extends Server {
    /**
     * 注册服务
     *
     * @param path      注册路径
     * @param discovery 数据
     * @return {@link ServiceDiscovery}
     */
    ServiceDiscovery registerService(String path, Discovery discovery);

    /**
     * 获取服务
     *
     * @param path    路径
     * @param balance 均衡算法实现
     * @return 结果
     */
    Discovery getService(String path, String balance);
    /**
     * 获取服务
     *
     * @param path    路径
     * @return 结果
     */
    default Discovery getService(String path) {
        return getService(path, "round");
    }
}
