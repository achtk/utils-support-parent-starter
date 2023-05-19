package com.chua.server.support.server;

import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.utils.StringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;

/**
 * netty服务
 *
 * @author CH
 * @since 2021-09-07
 */
public abstract class AbstractNettyServer extends AbstractServer {

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workGroup;

    public AbstractNettyServer(ServerOption serverOption, String... args) {
        super(serverOption);
    }

    @Override
    public void afterPropertiesSet() {
        bossGroup = new NioEventLoopGroup(request.getIntValue("max-total", 100));
        workGroup = new NioEventLoopGroup(request.getIntValue("max-idle", 100));
    }

    @Override
    public void run() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        for (Map.Entry<String, ChannelHandler> entry : channel().entrySet()) {
                            pipeline.addLast(entry.getKey(), entry.getValue());
                        }
                    }
                });

        serverBootstrap.option(ChannelOption.SO_BACKLOG, request.getIntValue("backlog", 1024));
        Channel channel = null;
        try {
            if (!StringUtils.isNullOrEmpty(getHost())) {
                channel = serverBootstrap.bind(getHost(), getPort()).sync().channel();
            } else {
                channel = serverBootstrap.bind(getPort()).sync().channel();
            }
            channel.closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    /**
     * 获取管道
     *
     * @return 管道
     */
    abstract protected Map<String, ChannelHandler> channel();

}
