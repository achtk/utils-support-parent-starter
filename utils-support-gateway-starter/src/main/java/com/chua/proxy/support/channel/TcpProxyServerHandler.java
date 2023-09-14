package com.chua.proxy.support.channel;

import com.chua.common.support.net.proxy.TcpProxyChannel;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.proxy.support.initializer.TcpConnectChannelInitializer;
import com.chua.proxy.support.listener.TcpChannelFutureListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * tcp代理服务器处理程序
 *
 * @author CH
 * @since 2023/09/13
 */
@Slf4j
public class TcpProxyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        List<TcpProxyChannel> collect = ServiceProvider.of(TcpProxyChannel.class).collect();
        //用工厂类构建bootstrap,用来建立socket连接
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(8))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60_000);
        bootstrap
                .handler(new TcpConnectChannelInitializer(ctx))
                .connect(new InetSocketAddress("127.0.0.1", 6379))
                .addListener(new TcpChannelFutureListener(msg, ctx));

    }
}
