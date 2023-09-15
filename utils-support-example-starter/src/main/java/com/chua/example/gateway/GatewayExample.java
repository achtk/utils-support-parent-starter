package com.chua.example.gateway;

import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.proxy.support.HttpProxyServerProvider;
import com.chua.proxy.support.TcpProxyServer;

/**
 * 网关示例
 *
 * @author CH
 * @since 2023/09/13
 */
public class GatewayExample {

    public static void main(String[] args) {
        Server httpProxyServer = new HttpProxyServerProvider().create(ServerOption.builder().build());
        httpProxyServer.start();
    }

    public static void tcp() {
        TcpProxyServer server = new TcpProxyServer(3333, 6379);
        server.start();
    }
}
