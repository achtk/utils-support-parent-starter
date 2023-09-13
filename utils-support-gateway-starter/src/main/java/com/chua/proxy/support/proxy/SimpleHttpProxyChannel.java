package com.chua.proxy.support.proxy;

import com.chua.common.support.net.proxy.HttpProxyChannel;
import com.chua.proxy.support.resolver.MappingResolver;
import com.chua.proxy.support.utils.FrameUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 简单http代理通道
 *
 * @author CH
 * @since 2023/09/13
 */
public class SimpleHttpProxyChannel implements HttpProxyChannel<FullHttpRequest, ByteBuf> {


    private final MappingResolver mappingResolver;

    public SimpleHttpProxyChannel(MappingResolver mappingResolver) {
        this.mappingResolver = mappingResolver;
    }

    @Override
    public ByteBuf proxy(FullHttpRequest req) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
//        final SslContext sslCtx;
//        if (SSL) {
//            SelfSignedCertificate ssc = new SelfSignedCertificate();
//            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
//        } else {
//            sslCtx = null;
//        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, mappingResolver.timeout())
                .remoteAddress(mappingResolver.resolve(FrameUtils.createFrame(req)))
        return null;
    }
}
