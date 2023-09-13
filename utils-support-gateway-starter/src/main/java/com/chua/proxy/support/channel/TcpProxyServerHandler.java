package com.chua.proxy.support.channel;

import com.chua.common.support.net.proxy.TcpProxyChannel;
import com.chua.common.support.spi.ServiceProvider;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * tcp代理服务器处理程序
 *
 * @author CH
 * @since 2023/09/13
 */
@Slf4j
public class TcpProxyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        List<TcpProxyChannel> collect = ServiceProvider.of(TcpProxyChannel.class).collect();
        //TODO:
    }
}
