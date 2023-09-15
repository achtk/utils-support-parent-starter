package com.chua.proxy.support.route.locator;

import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.discovery.ServiceDiscovery;
import com.chua.proxy.support.route.Route;
import reactor.core.publisher.Flux;

/**
 * 动态路由查找器
 * @author CH
 */
public class DynamicPathRouteLocator implements RouteLocator{

    private ServiceDiscovery serviceDiscovery;

    public DynamicPathRouteLocator(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Flux<Route> getRoutes(String path) {
        Discovery discovery = serviceDiscovery.getService(path);
        if(null == discovery) {
            return Flux.empty();
        }

        return Flux.empty();
    }
}
