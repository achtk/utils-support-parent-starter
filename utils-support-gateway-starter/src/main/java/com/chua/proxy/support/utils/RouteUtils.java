package com.chua.proxy.support.utils;

import com.chua.proxy.support.exchange.Exchange;
import com.chua.proxy.support.route.Route;

/**
 * Utils路线
 *
 * @author CH
 */
public class RouteUtils {

    private static final String KEY = "__route__";

    public static void setRoute(Exchange exchange, Route route) {
        exchange.getAttributes().put(KEY, route);
    }

    public static Route getRoute(Exchange exchange) {
        return exchange.getRequiredAttribute(KEY);
    }

}
