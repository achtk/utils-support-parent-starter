package com.chua.proxy.support;

import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.proxy.support.endpoint.GatewayInternalEndpoint;
import com.chua.proxy.support.handler.HttpHandler;
import com.chua.proxy.support.route.locator.CompositeRouteLocator;
import com.chua.proxy.support.route.locator.GatewayInternalRouteLocator;
import com.chua.proxy.support.route.locator.RouteLocator;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * http
 * @author CH
 */
public class HttpProxyServer extends AbstractServer {
    private RouteLocator routeLocator;
    private DisposableServer disposableServer;
    private final AtomicBoolean running = new AtomicBoolean(false);

    protected HttpProxyServer(ServerOption serverOption) {
        super(serverOption);
    }

    public HttpProxyServer(ServerOption serverOption, RouteLocator routeLocator) {
        super(serverOption);
        this.routeLocator = routeLocator;
    }

    public HttpProxyServer(String host, RouteLocator routeLocator) {
        this(ServerOption.builder().host(host).build(), routeLocator);
    }

    public HttpProxyServer(int port, RouteLocator routeLocator) {
        this(ServerOption.builder().port(port).build(), routeLocator);
    }

    public HttpProxyServer(String host, int port, RouteLocator routeLocator) {
        this(ServerOption.builder().host(host).port(port).build(), routeLocator);
    }

    @Override
    public void afterPropertiesSet() {
        if(null == routeLocator) {
            this.routeLocator = new CompositeRouteLocator(
                    new GatewayInternalRouteLocator(new GatewayInternalEndpoint("/"))
            );
        }
    }

    @Override
    protected void shutdown() {
        disposableServer.disposeNow();
        running.set(false);
    }

    @Override
    protected void run() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        this.disposableServer = HttpServer.create()
                .accessLog(true)
                .host(getHost()).port(getPort())
                .handle(new HttpHandler(routeLocator))
                .bindNow();

        disposableServer.onDispose().block();
    }


}
