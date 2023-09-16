package com.chua.example.gateway;

import com.chua.common.support.discovery.DiscoveryOption;
import com.chua.common.support.discovery.MulticastServiceDiscovery;
import com.chua.proxy.support.HttpProxyServer;
import com.chua.proxy.support.TcpProxyServer;
import com.chua.proxy.support.factory.DiscoveryServiceChannelFactory;
import com.chua.proxy.support.factory.ProxyChannelFactory;
import com.google.common.collect.Lists;

import java.io.IOException;

/**
 * 网关示例
 *
 * @author CH
 * @since 2023/09/13
 */
public class GatewayExample {

    public static void main(String[] args) throws IOException {
        HttpProxyServer httpProxyServer = new HttpProxyServer(3333,
                Lists.newArrayList(
                        new DiscoveryServiceChannelFactory(new MulticastServiceDiscovery(new DiscoveryOption().setAddress("224.0.0.1:2111"))),
                        new ProxyChannelFactory()
                )
        );
        httpProxyServer.start();
    }

    public static void tcp() {
        TcpProxyServer server = new TcpProxyServer(3333, 3306);
        server.start();
    }
}
