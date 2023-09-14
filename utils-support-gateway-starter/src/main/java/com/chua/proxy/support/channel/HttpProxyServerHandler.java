package com.chua.proxy.support.channel;

import com.chua.common.support.net.channel.limit.LimitChannel;
import com.chua.common.support.net.channel.limit.LimitConfig;
import com.chua.common.support.net.proxy.HttpProxyChannel;
import com.chua.common.support.net.resolver.MappingResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.proxy.support.context.ProxyContext;
import com.chua.proxy.support.utils.FrameUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * http代理服务器处理程序
 *
 * @author CH
 * @since 2023/09/13
 */
public class HttpProxyServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        //处理100 continue请求
        if (is100ContinueExpected(msg)) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));

            ctx.pipeline().remove("httpCodec");
            ctx.pipeline().remove("httpObject");
            return;
        }

        if (isLimit(ctx, msg)) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.TOO_MANY_REQUESTS));
            return;
        }

        HttpProxyChannel channel = ServiceProvider.of(HttpProxyChannel.class).getNewExtension("http-proxy", ctx,
                ProxyContext.getInstance().getBean(ProxyContext.getInstance().getMappingConfig().getName(), MappingResolver.class));
        channel.proxy(msg);
        //TODO:
    }

    /**
     * limit
     *
     * @param ctx     ctx
     * @param request msg
     * @return boolean
     */
    private boolean isLimit(ChannelHandlerContext ctx, FullHttpRequest request) {
        LimitConfig limitConfig = ProxyContext.getInstance().getLimitConfig();
        LimitChannel limitChannel = ServiceProvider.of(LimitChannel.class).getNewExtension(limitConfig.getName(), limitConfig);
        if (null == limitChannel) {
            return false;
        }
        return limitChannel.tryAcquire(FrameUtils.createFrame(request));
    }
}
