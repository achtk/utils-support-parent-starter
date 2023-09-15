package com.chua.proxy.support.route.locator;

import com.chua.proxy.support.route.Route;
import reactor.core.publisher.Flux;


/**
 * 复合路线定位器
 *
 * @author CH
 */
public class CompositeRouteLocator implements RouteLocator {

    private final Flux<RouteLocator> delegates;

    public CompositeRouteLocator(Flux<RouteLocator> delegates) {
        this.delegates = delegates;
    }

    public CompositeRouteLocator(RouteLocator... routeLocators) {
        this(Flux.fromArray(routeLocators));
    }

    @Override
    public Flux<Route> getRoutes(String path) {
        return this.delegates.flatMapSequential(routeLocator -> routeLocator.getRoutes(path));
    }

}
