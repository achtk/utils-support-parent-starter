package com.chua.proxy.support.channel;

import com.chua.common.support.net.proxy.HttpProxyChannel;
import com.chua.common.support.spi.ServiceProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.List;

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
        }
        List<HttpProxyChannel> collect = ServiceProvider.of(HttpProxyChannel.class).collect();
        //TODO:
    }
}
