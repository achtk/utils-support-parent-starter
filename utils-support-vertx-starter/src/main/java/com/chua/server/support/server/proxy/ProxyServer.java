package com.chua.server.support.server.proxy;

import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.impl.SocketAddressImpl;
import io.vertx.core.streams.Pump;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 代理
 *
 * @author CH
 */
@Slf4j
public class ProxyServer extends AbstractServer {

    private NetServer netServer;
    private NetClient netClient;

    protected ProxyServer(ServerOption serverOption, String... args) {
        super(serverOption);
    }

    @Override
    public void run() {
        this.netServer = Vertx.vertx().createNetServer();
        this.netClient = Vertx.vertx().createNetClient();

        String serverAddress = request.getString( "proxy-address");
        int serverPort = request.getIntValue("proxy-port", 5555);
        if (StringUtils.isNullOrEmpty(serverAddress)) {
            log.warn("请设置代理地址, ream.proxy-address");
        }

        if (serverPort == 0) {
            log.warn("请设置代理端口, ream.proxy-port");
        }

        netServer.connectHandler(clientSocket -> {

            log.info("客户端 {}:{} 创建连接", clientSocket.remoteAddress().host(), clientSocket.remoteAddress().port());
            netClient.connect(serverPort, serverAddress, result -> {
                if (result.succeeded()) {
                    NetSocket proxySocket = result.result();
                    log.info("代理连接成功");
                    Pump.pump(clientSocket, proxySocket).start();
                    Pump.pump(proxySocket, clientSocket).start();
                    proxySocket.closeHandler(event -> {
                        if (log.isDebugEnabled()) {
                            log.debug("代理连接关闭");
                        }
                    });
                } else {
                    log.error("代理连接失败");
                }
                clientSocket.closeHandler(event -> {
                    log.info("客户端 {}:{} 断开连接", clientSocket.remoteAddress().host(), clientSocket.remoteAddress().port());
                });
            });
        });
        int port = getPort();
        String host = getHost();
        InetSocketAddress inetSocketAddress;
        if (StringUtils.isNullOrEmpty(host)) {
            inetSocketAddress = new InetSocketAddress(port);
        } else {
            inetSocketAddress = new InetSocketAddress(host, port);
        }
        netServer.listen(new SocketAddressImpl(inetSocketAddress));
    }

    @Override
    public void shutdown() {
        if (null != netClient) {
            netClient.close();
        }
        if (null != netServer) {
            netServer.close();
        }
    }

    @Override
    public void afterPropertiesSet() {

    }
}
