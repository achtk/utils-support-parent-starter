package com.chua.example.gateway;

import com.chua.common.support.discovery.DiscoveryOption;
import com.chua.common.support.discovery.MulticastServiceDiscovery;
import com.chua.proxy.support.HttpProxyServer;
import com.chua.proxy.support.TcpProxyServer;
import com.chua.proxy.support.endpoint.GatewayInternalEndpoint;
import com.chua.proxy.support.route.locator.CompositeRouteLocator;
import com.chua.proxy.support.route.locator.DynamicPathRouteLocator;
import com.chua.proxy.support.route.locator.GatewayInternalRouteLocator;

import java.io.IOException;

/**
 * 网关示例
 *
 * @author CH
 * @since 2023/09/13
 */
public class GatewayExample {

    public static void main(String[] args) throws IOException {
        HttpProxyServer httpProxyServer = new HttpProxyServer(3333, new CompositeRouteLocator(
                new GatewayInternalRouteLocator(new GatewayInternalEndpoint("/")),
                new DynamicPathRouteLocator(new MulticastServiceDiscovery(new DiscoveryOption()))
        ));
        httpProxyServer.start();
    }

    public static void tcp() {
        TcpProxyServer server = new TcpProxyServer(3333, 6379);
        server.start();
    }
}
