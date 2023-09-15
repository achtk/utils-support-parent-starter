package com.chua.proxy.support.route.locator;

import com.chua.common.support.utils.PathUtils;
import com.chua.proxy.support.endpoint.GatewayInternalEndpoint;
import com.chua.proxy.support.route.Route;
import com.google.common.collect.Lists;
import reactor.core.publisher.Flux;

/**
 * 网关内部路由定位器
 *
 * @author CH
 */
@SuppressWarnings("java:S1075")
public class GatewayInternalRouteLocator implements RouteLocator {

    public static final String INTERNAL_CONTEXT_PATH = "/__internal";

    private static final String INTERNAL_CONTEXT_PATH_SLASH = INTERNAL_CONTEXT_PATH + "/";

    private final Flux<Route> internalRoutes;

    public GatewayInternalRouteLocator(GatewayInternalEndpoint gatewayInternalEndpoint) {
        Route route = new Route();
        route.setId("__internal__");
        route.setPath(INTERNAL_CONTEXT_PATH);
        route.setFilters(Lists.newArrayList(gatewayInternalEndpoint));
        this.internalRoutes = Flux.just(route);
    }

    @Override
    public Flux<Route> getRoutes(String path) {
        String normalizePath = PathUtils.normalize(path);
        if (normalizePath.startsWith(INTERNAL_CONTEXT_PATH_SLASH) || normalizePath.equals(INTERNAL_CONTEXT_PATH)) {
            return internalRoutes;
        }
        return Flux.empty();
    }

}
