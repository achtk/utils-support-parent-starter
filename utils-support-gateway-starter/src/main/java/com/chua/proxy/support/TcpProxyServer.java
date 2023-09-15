package com.chua.proxy.support;

import com.chua.common.support.protocol.server.Server;
import io.netty.buffer.ByteBuf;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.util.function.Consumer;

/**
 * http
 * @author CH
 */
public class TcpProxyServer implements Server {
    private DisposableServer disposableServer;
    private int port;

    private String host;

    private String targetHost;
    private int targetTcp;

    public TcpProxyServer(int port, String host, String targetHost, int targetTcp) {
        this.port = port;
        this.host = host;
        this.targetHost = targetHost;
        this.targetTcp = targetTcp;
    }

    public TcpProxyServer(int port, String targetHost, int targetTcp) {
       this(port, "0.0.0.0", targetHost, targetTcp);
    }
    public TcpProxyServer(int port, int targetTcp) {
       this(port, "0.0.0.0", "127.0.0.1", targetTcp);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void start() {
        this.disposableServer = TcpServer.create()
                .host(this.host)
                .port(this.port)
                .doOnConnection(server -> TcpClient.create().host(targetHost).port(targetTcp)
                        .connect()
                        .subscribe((client) -> {
                            final Disposable reqDispose;
                            final Disposable respDispose;
                            final Disposable bridgeDispose;
                            reqDispose = decoratedBridge(server.inbound(), client.outbound(), byteBuf -> {});
                            respDispose = decoratedBridge(client.inbound(), server.outbound(), byteBuf -> { });
                            bridgeDispose = Disposables.composite(reqDispose, respDispose, client.channel()::close);
                            server.onDispose(bridgeDispose);
                        }))
                .bindNow();

        disposableServer.onDispose().block();
    }

    private final Disposable decoratedBridge(final NettyInbound inbound, final NettyOutbound outbound,
                                             final Consumer<? super ByteBuf> decorator) {
        return outbound.send(inbound.receive()
                        .retain()
                        .doOnNext(decorator))
                .then()
                .subscribe();
    }

    @Override
    public Server register(Object bean) {
        return null;
    }

    @Override
    public Server register(String name, Object bean) {
        return null;
    }

    @Override
    public void close() {
        disposableServer.disposeNow();
    }
}
