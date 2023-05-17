package com.chua.common.support.protocol.discovery;


import com.chua.common.support.lang.robin.Robin;

/**
 * 服务发现
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
public interface ServiceDiscovery {

    /**
     * 负载均衡
     *
     * @param robin 负载均衡
     * @return this
     */
    ServiceDiscovery robin(Robin robin);

    /**
     * 发现目录
     *
     * @param discovery 发现目录
     * @return this
     * @throws Exception e
     */
    default Discovery discovery(String discovery) throws Exception {
        return discovery(discovery, null);
    }

    /**
     * 发现服务
     *
     * @param discovery 发现目录
     * @param strategy  策略
     * @return this
     * @throws Exception e
     */
    Discovery discovery(String discovery, DiscoveryBoundType strategy) throws Exception;
    /**
     * 注册
     *
     * @param discovery 发现器
     * @return this
     */
    ServiceDiscovery register(Discovery discovery);

    /**
     * 启动发现服务
     *
     * @return this
     * @throws Exception e
     */
    default ServiceDiscovery start() throws Exception {
        return start(new DiscoveryOption().setAddress("127.0.0.1:2181"));
    }

    /**
     * 启动发现服务
     *
     * @param discoveryOption 配置
     * @return this
     * @throws Exception e
     */
    ServiceDiscovery start(DiscoveryOption discoveryOption) throws Exception;

    /**
     * 关闭发现服务
     *
     * @return this
     * @throws Exception e
     */
    ServiceDiscovery stop() throws Exception;
}
