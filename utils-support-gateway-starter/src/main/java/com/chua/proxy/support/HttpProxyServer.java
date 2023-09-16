package com.chua.proxy.support;

import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.utils.StringUtils;
import com.chua.proxy.support.factory.ChannelFactory;
import com.chua.proxy.support.filter.Filter;
import com.chua.proxy.support.initializer.ProxyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * http
 *
 * @author CH
 */
@Slf4j
public class HttpProxyServer extends AbstractServer {
    private List<ChannelFactory> channelFactoryList;
    private Filter[] filter;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    final ServerBootstrap serverBootstrap = new ServerBootstrap();
    ChannelFuture channelFuture = null;
    private final AtomicBoolean running = new AtomicBoolean(false);

    protected HttpProxyServer(ServerOption serverOption) {
        super(serverOption);
    }

    public HttpProxyServer(ServerOption serverOption, List<ChannelFactory> channelFactoryList, Filter... filters) {
        super(serverOption);
        this.channelFactoryList = channelFactoryList;
        this.filter = filters;
    }

    public HttpProxyServer(String host, List<ChannelFactory> channelFactoryList, Filter... filters) {
        this(ServerOption.builder().host(host).build(), channelFactoryList, filters);
    }

    public HttpProxyServer(int port, List<ChannelFactory> channelFactoryList, Filter... filters) {
        this(ServerOption.builder().port(port).build(), channelFactoryList, filters);
    }

    public HttpProxyServer(String host, int port, List<ChannelFactory> channelFactoryList, Filter... filters) {
        this(ServerOption.builder().host(host).port(port).build(), channelFactoryList, filters);
    }

    @Override
    public void afterPropertiesSet() {
        bossGroup = new NioEventLoopGroup(getServerOption().maxTotal());
        workerGroup = new NioEventLoopGroup(getServerOption().maxIdle());
    }

    private void registerChannel() throws InterruptedException {
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, getServerOption().backlog())
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ProxyChannelInitializer(getServerOption(), channelFactoryList, filter))
                .childOption(ChannelOption.TCP_NODELAY, getServerOption().tcpNoDelay());

        if (StringUtils.isNotEmpty(getHost())) {
            channelFuture = serverBootstrap.bind(getHost(), getPort());
        } else {
            channelFuture = serverBootstrap.bind(getPort());
        }

        channelFuture = channelFuture.sync().channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {

        });
    }

    @Override
    protected void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        channelFuture.channel().close();
        log.info("代理服务器[{}]已停止 >>>>>>>>>>>>>>>", getPort());
        running.set(false);
    }

    @Override
    protected void run() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        try {
            registerChannel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
