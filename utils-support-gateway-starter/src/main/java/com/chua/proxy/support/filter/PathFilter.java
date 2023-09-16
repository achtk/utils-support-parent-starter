package com.chua.proxy.support.filter;

import com.chua.common.support.discovery.ServiceDiscovery;

import java.io.IOException;

/**
 * 路径滤波器
 *
 * @author CH
 * @since 2023/09/16
 */
public class PathFilter implements Filter {

    private ServiceDiscovery serviceDiscovery;

    public PathFilter(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        try {
            serviceDiscovery.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
