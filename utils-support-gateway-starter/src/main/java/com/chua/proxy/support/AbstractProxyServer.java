package com.chua.proxy.support;

import com.chua.common.support.utils.StringUtils;
import com.chua.proxy.support.config.ProxyConfig;
import com.chua.proxy.support.initializer.ProxyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * 代理服务器
 *
 * @author CH
 * @since 2023/09/13
 */
@Slf4j
public abstract class AbstractProxyServer implements ProxyServer {

    protected ProxyConfig proxyConfig;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    final ServerBootstrap serverBootstrap = new ServerBootstrap();
    ChannelFuture channelFuture = null;

    public AbstractProxyServer(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    @Override
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(proxyConfig.bossWorkNum());
        workerGroup = new NioEventLoopGroup();
        registerChannel();
    }

    private void registerChannel() throws InterruptedException {
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, proxyConfig.backlog())
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ProxyChannelInitializer(proxyConfig))
                .childOption(ChannelOption.TCP_NODELAY, proxyConfig.tcpNoDelay());

        log.info("=======================================================");
        if (StringUtils.isNotEmpty(proxyConfig.host())) {
            channelFuture = serverBootstrap.bind(proxyConfig.host(), proxyConfig.port());
            log.info("代理服务器[{}: {}]启动 >>>>>>>>>>>>>>>", proxyConfig.host(), proxyConfig.port());
        } else {
            log.info("代理服务器[{}]启动 >>>>>>>>>>>>>>>", proxyConfig.port());
            channelFuture = serverBootstrap.bind(proxyConfig.port());
        }

        log.info("=======================================================");
        channelFuture = channelFuture.sync().channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {

        });
    }

    @Override
    public boolean isRunning() {
        return channelFuture.channel().isActive();
    }

    @Override
    public void stop() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        channelFuture.channel().close();
        log.info("代理服务器[{}]已停止 >>>>>>>>>>>>>>>", proxyConfig.port());

    }
}
