package com.chua.proxy.support.proxy;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.net.proxy.HttpProxyChannel;
import com.chua.common.support.net.resolver.MappingResolver;
import com.chua.proxy.support.initializer.HttpConnectChannelInitializer;
import com.chua.proxy.support.initializer.HttpsConnectChannelInitializer;
import com.chua.proxy.support.listener.HttpChannelFutureListener;
import com.chua.proxy.support.listener.HttpsChannelFutureListener;
import com.chua.proxy.support.utils.FrameUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 简单http代理通道
 *
 * @author CH
 * @since 2023/09/13
 */
@Spi("http-proxy")
public class SimpleHttpProxyChannel implements HttpProxyChannel {


    private final MappingResolver mappingResolver;
    private final Channel clientChannel;
    private final ChannelHandlerContext ctx;

    public SimpleHttpProxyChannel(ChannelHandlerContext ctx, MappingResolver mappingResolver) {
        this.mappingResolver = mappingResolver;
        this.ctx = ctx;
        this.clientChannel = ctx.channel();
    }

    @Override
    public void proxy(Object req1) {
        FullHttpRequest req = (FullHttpRequest) req1;
        Discovery discovery = mappingResolver.resolve(FrameUtils.createFrame(req));
        if (null == discovery) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.NOT_FOUND));
            return;
        }
        req.headers().set("Host", discovery.getAddress() + ":" + discovery.getPort());

        //用工厂类构建bootstrap,用来建立socket连接
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(8))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, mappingResolver.timeout());
        //如果是http请求
        if (discovery.isHttp()) {
            //添加监听器,当连接建立成功后,转发客户端的消息给它
            bootstrap
                    .handler(new HttpConnectChannelInitializer(ctx))
                    .connect(discovery.address())
                    .addListener(new HttpChannelFutureListener(req, ctx));
            return;
        }
        //如果是Https请求
        bootstrap
                .handler(new HttpsConnectChannelInitializer(ctx))
                .connect(discovery.address())
                .addListener(new HttpsChannelFutureListener(req, ctx));

    }
}
