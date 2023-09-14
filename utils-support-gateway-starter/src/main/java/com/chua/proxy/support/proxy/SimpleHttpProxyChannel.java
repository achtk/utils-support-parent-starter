package com.chua.proxy.support.proxy;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.net.proxy.HttpProxyChannel;
import com.chua.common.support.net.resolver.MappingResolver;
import com.chua.proxy.support.utils.FrameUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

/**
 * 简单http代理通道
 *
 * @author CH
 * @since 2023/09/13
 */
@Spi("http-proxy")
public class SimpleHttpProxyChannel implements HttpProxyChannel<FullHttpRequest, ByteBuf> {


    private final MappingResolver mappingResolver;
    private final Channel clientChannel;

    public SimpleHttpProxyChannel(ChannelHandlerContext ctx, MappingResolver mappingResolver) {
        this.mappingResolver = mappingResolver;
        this.clientChannel = ctx.channel();
    }

    @Override
    public ByteBuf proxy(FullHttpRequest req) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, mappingResolver.timeout())
                .handler(new HttpRequestEncoder());

        ChannelFuture channelFuture = bootstrap.connect(mappingResolver.resolve(FrameUtils.createFrame(req)));
        Channel remoteChannel = channelFuture.channel();
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                // connection is ready, enable AutoRead
                clientChannel.config().setAutoRead(true);

                // 普通http请求解析了第一个完整请求，第一个请求也要原样发送到远端服务器
                remoteChannel.writeAndFlush(req);

                /**
                 * 第一个完整Http请求处理完毕后，不需要解析任何 Http 数据了，直接盲目转发 TCP 流就行了
                 * 所以无论是连接客户端的 clientChannel 还是连接远端主机的 remoteChannel 都只需要一个 RelayHandler 就行了。
                 * 代理服务器在中间做转发。
                 *
                 * 客户端   --->  clientChannel --->  代理 ---> remoteChannel ---> 远端主机
                 * 远端主机 --->  remoteChannel  --->  代理 ---> clientChannel ---> 客户端
                 */
                clientChannel.pipeline().remove(HttpRequestDecoder.class);
                clientChannel.pipeline().remove(HttpResponseEncoder.class);
                clientChannel.pipeline().remove(HttpObjectAggregator.class);
//                clientChannel.pipeline().remove(HttpProxyClientHandler.this);
//                clientChannel.pipeline().addLast(new RelayHandler(remoteChannel));

                remoteChannel.pipeline().remove(HttpRequestEncoder.class);
//                remoteChannel.pipeline().addLast(new RelayHandler(clientChannel));
            } else {
                clientChannel.close();
            }
        });
        return null;
    }
}
