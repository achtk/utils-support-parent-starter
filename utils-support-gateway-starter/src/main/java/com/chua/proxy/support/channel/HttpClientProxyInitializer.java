package com.chua.proxy.support.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;

/**
 * @author CH
 */
public class HttpClientProxyInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandlerContext ctx;
    private final Channel clientChannel;
    private final boolean isHttp;
    static LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
    public static SslContext sslContext;
    static  SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
    {
        //下面这行，直接信任自签证书
        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
        try {
            sslContext = sslContextBuilder.build();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpClientProxyInitializer(ChannelHandlerContext ctx, Channel clientChannel, boolean isHttp) {
        this.ctx = ctx;
        this.clientChannel = clientChannel;
        this.isHttp = isHttp;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        if(!isHttp) {
            ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
        }
        ctx.pipeline().remove("httpCodec");
        ctx.pipeline().remove("httpObject");
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(1048576*10));
        ch.pipeline().addLast(new HttpContentDecompressor());
        ch.pipeline().addLast(new HttpClientProxyHandler(clientChannel));
    }
}
