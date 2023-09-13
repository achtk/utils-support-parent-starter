package com.chua.proxy.support.channel;

import com.chua.proxy.support.config.ProxyConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 代理通道初始值设定项
 *
 * @author CH
 * @since 2023/09/13
 */
public class ProxyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ProxyConfig proxyConfig;

    public ProxyChannelInitializer(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                //闲置连接回收
                .addLast(new IdleStateHandler(proxyConfig.readerIdleTime(), proxyConfig.writerIdleTime(), proxyConfig.allIdleTime(), TimeUnit.SECONDS))
                //inbound流日志记录
                .addLast(new TcpInLogHandler())
                //outbound流日志记录
                .addLast(new TcpOutLogHandler())
                //业务处理
                .addLast(new DispatcherHandler(ch, proxyConfig));
    }
}
