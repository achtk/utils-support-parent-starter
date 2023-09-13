package com.chua.proxy.support.channel;

import com.chua.proxy.support.config.ProxyConfig;
import com.chua.proxy.support.decoder.AbstractMessageDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 调度员处理程序
 *
 * @author CH
 * @since 2023/09/13
 */
public class DispatcherHandler extends AbstractMessageDecoder {
    private final SocketChannel ch;
    private final ProxyConfig proxyConfig;

    public DispatcherHandler(SocketChannel ch, ProxyConfig proxyConfig) {
        super();
        this.ch = ch;
        this.proxyConfig = proxyConfig;
    }

    @Override
    public void fullyConnect(ChannelHandlerContext ctx) throws Exception {
        //发送启动代理客户端命令

    }

    @Override
    public void addHttpHandler(ChannelHandlerContext ctx) {
        // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
        ch.pipeline().addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(512 * 1024))
                .addLast(new HttpProxyServerHandler());
    }

    @Override
    public void addTcpHandler(ChannelHandlerContext ctx) {
        ctx.pipeline()
                //闲置连接回收
                .addLast(new IdleStateHandler(proxyConfig.readerIdleTime(), proxyConfig.writerIdleTime(), proxyConfig.allIdleTime(), TimeUnit.SECONDS))
                //inbound流日志记录
                .addLast(new TcpInLogHandler())
                //outbound流日志记录
                .addLast(new TcpOutLogHandler())
                //业务处理
                .addLast(new TcpProxyServerHandler());
    }
}
