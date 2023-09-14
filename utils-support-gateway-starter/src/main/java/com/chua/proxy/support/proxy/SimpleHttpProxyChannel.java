package com.chua.proxy.support.proxy;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.net.proxy.HttpProxyChannel;
import com.chua.common.support.net.resolver.MappingResolver;
import com.chua.proxy.support.channel.HttpClientProxyInitializer;
import com.chua.proxy.support.utils.FrameUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
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
public class SimpleHttpProxyChannel implements HttpProxyChannel<FullHttpRequest> {


    private final MappingResolver mappingResolver;
    private final Channel clientChannel;
    private final ChannelHandlerContext ctx;

    public SimpleHttpProxyChannel(ChannelHandlerContext ctx, MappingResolver mappingResolver) {
        this.mappingResolver = mappingResolver;
        this.ctx = ctx;
        this.clientChannel = ctx.channel();
    }

    @Override
    public void proxy(FullHttpRequest req) {
        Discovery discovery = mappingResolver.resolve(FrameUtils.createFrame(req));
        if(null == discovery) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.NOT_FOUND));
            return ;
        }

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new HttpClientProxyInitializer(ctx, ctx.channel(), discovery.isHttp()));
        ChannelFuture cf = bootstrap.connect(discovery.getAddress(), discovery.getPort());
        cf.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    future.channel().writeAndFlush(req);
                } else {
                    ctx.channel().close();
                }
            }
        });
    }
}
