package com.chua.proxy.support.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CH
 */
@Slf4j
public class HttpClientProxyHandler extends ChannelInboundHandlerAdapter {

    private final Channel clientChannel;

    public HttpClientProxyHandler(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("客户端消息："+msg.toString());
        FullHttpResponse response = (FullHttpResponse) msg;

        clientChannel.writeAndFlush(response);
    }
}
