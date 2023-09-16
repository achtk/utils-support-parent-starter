package com.chua.proxy.support.initializer;

import com.chua.common.support.protocol.server.ServerOption;
import com.chua.proxy.support.channel.DispatcherHandler;
import com.chua.proxy.support.channel.TcpInLogHandler;
import com.chua.proxy.support.channel.TcpOutLogHandler;
import com.chua.proxy.support.factory.ChannelFactory;
import com.chua.proxy.support.filter.Filter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 代理通道初始值设定项
 *
 * @author CH
 * @since 2023/09/13
 */
public class ProxyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ServerOption serverOption;
    private final List<ChannelFactory> channelFactoryList;
    private final Filter[] filter;

    public ProxyChannelInitializer(ServerOption serverOption, List<ChannelFactory> channelFactoryList, Filter[] filter) {
        this.serverOption = serverOption;
        this.channelFactoryList = channelFactoryList;
        this.filter = filter;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                //闲置连接回收
                .addLast(new IdleStateHandler(serverOption.readerIdleTime(), serverOption.writerIdleTime(), serverOption.allIdleTime(), TimeUnit.SECONDS))
                //inbound流日志记录
                .addLast(new TcpInLogHandler())
                //outbound流日志记录
                .addLast(new TcpOutLogHandler())
                //业务处理
                .addLast(new DispatcherHandler(ch, serverOption, channelFactoryList, filter));
    }
}
