package com.chua.proxy.support.handler;

import com.chua.proxy.support.context.ReactiveRequestContextHolder;
import com.chua.proxy.support.context.RequestContext;
import com.chua.proxy.support.context.RequestContextImpl;
import com.chua.proxy.support.exchange.ChainBasedExchangeHandler;
import com.chua.proxy.support.exchange.Exchange;
import com.chua.proxy.support.exchange.ExchangeImpl;
import com.chua.proxy.support.filter.Filter;
import com.chua.proxy.support.filter.FilterChain;
import com.chua.proxy.support.route.Route;
import com.chua.proxy.support.route.locator.RouteLocator;
import com.chua.proxy.support.utils.ResponseUtils;
import com.chua.proxy.support.utils.RouteUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Objects;
import java.util.function.BiFunction;


/**
 * httpHandler
 * @author CH
 */
@Slf4j
public class HttpHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>> {
    private final RouteLocator routeLocator;

    private static final Route ROUTE_404;

    static {
        ROUTE_404 = new Route();
        ROUTE_404.setId("404");
        ROUTE_404.setPath("/");
        ROUTE_404.setFilters(Lists.newArrayList(new Filter404()));
    }


    public HttpHandler(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @Override
    public Mono<Void> apply(HttpServerRequest request, HttpServerResponse response) {
        Exchange exchange = new ExchangeImpl(request, response);
        return routeLocator.getRoutes(request.fullPath())
                .filter(
                        route -> Objects.nonNull(route.getMethods())
                                && route.getMethods().contains(request.method())
                                && route.isMatch(exchange)
                )
                .switchIfEmpty(Mono.just(ROUTE_404))
                .next()
                .flatMap(route -> {
                    RouteUtils.setRoute(exchange, route);
                    Mono<RequestContext> requestContext = Mono.just(new RequestContextImpl(exchange));
                    return new ChainBasedExchangeHandler(route.getFilters())
                            .handle(exchange)
                            .contextWrite(ReactiveRequestContextHolder.withRequestContext(requestContext));
                })
                .onErrorResume(throwable -> {
                    log.error("Request handle failed.", throwable);
                    return ResponseUtils.sendError(response);
                });
    }

    /**
     * 过滤器404
     *
     * @author CH
     */
    private static class Filter404 implements Filter {
        @Override
        public Mono<Void> filter(Exchange exchange, FilterChain chain) {
            return ResponseUtils.sendNotFound(exchange.getResponse());
        }
    }
}
