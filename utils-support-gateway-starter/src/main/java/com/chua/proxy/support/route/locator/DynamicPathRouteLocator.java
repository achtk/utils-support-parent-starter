package com.chua.proxy.support.route.locator;

import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.discovery.ServiceDiscovery;
import com.chua.common.support.utils.StringUtils;
import com.chua.proxy.support.filter.HttpClientProxyFilter;
import com.chua.proxy.support.route.Route;
import com.google.common.collect.Lists;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.constant.NameConstant.HTTP;

/**
 * 动态路由查找器
 * @author CH
 */
public class DynamicPathRouteLocator implements RouteLocator{

    private ServiceDiscovery serviceDiscovery;

    public DynamicPathRouteLocator(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        try {
            serviceDiscovery.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Flux<Route> getRoutes(String path) {
        Discovery discovery = serviceDiscovery.getService(path);
        if(null == discovery) {
            return Flux.empty();
        }

        return Flux.concat(converterRoutes(discovery));
    }

    /**
     * 转换器路线
     *
     * @param discovery 发现
     * @return {@link List}<{@link Flux}<{@link Route}>>
     */
    private List<Flux<Route>> converterRoutes(Discovery discovery) {
        List<Flux<Route>> rs = new LinkedList<>();
        rs.add(Flux.just(create(discovery)));
        return rs;
    }

    /**
     * 创造
     *
     * @param discovery 发现
     * @return {@link Route}
     */
    private Route create(Discovery discovery) {
        Route route = new Route();
        route.setPath(
                StringUtils.defaultString(discovery.getProtocol(), HTTP) + "://" +
                discovery.getIp() + ":" + discovery.getPort() + "" + discovery.getUriSpec()
        );
        route.setTimeout(discovery.getTimeout());
        route.setFilters(Lists.newArrayList(new HttpClientProxyFilter(route)));
        return route;
    }
}
