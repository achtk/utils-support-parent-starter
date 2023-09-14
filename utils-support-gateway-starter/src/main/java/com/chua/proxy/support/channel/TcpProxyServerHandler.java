package com.chua.proxy.support.channel;

import com.chua.common.support.net.proxy.TcpProxyChannel;
import com.chua.common.support.spi.ServiceProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

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
        //TODO:
    }
}
