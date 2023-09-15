package com.chua.proxy.support.route;

import com.chua.proxy.support.definition.IdRouteDefinition;
import com.chua.proxy.support.definition.RouteDefinition;
import com.chua.proxy.support.filter.Filter;
import io.netty.handler.codec.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 路线转换器
 *
 * @author CH
 */
public class RouteConverter {

    private RouteConverter() {
    }


    public static Route convertRouteDefinition(IdRouteDefinition idRouteDefinition) throws Exception {
        Route route = new Route();
        route.setId(idRouteDefinition.getId());

        RouteDefinition routeDefinition = idRouteDefinition.getRouteDefinition();
        route.setPath(routeDefinition.getPath());
        Set<HttpMethod> httpMethods = new HashSet<>(4);
        for (String method : routeDefinition.getMethods()) {
            httpMethods.add(HttpMethod.valueOf(method.toUpperCase()));
        }
        route.setMethods(httpMethods);

        // 暂不解析predicateDefinitions
        List<Filter> filters = new ArrayList<>(routeDefinition.getPluginDefinitions().size());


        route.setFilters(filters);

        // route需要用组织id当作自己的查找环境变量的key
        route.setEnvKey(idRouteDefinition.getOrgId());
        return route;
    }


}
