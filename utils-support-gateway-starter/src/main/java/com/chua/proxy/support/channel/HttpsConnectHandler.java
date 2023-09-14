package com.chua.proxy.support.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于将客户端发送的https请求和目标主机建立连接后,
 * 处理目标主机的输入事件的处理器
 * <p>
 * 每建立一个连接,都需要创建一个该对象
 *
 * @author CH
 * @since 2023/09/14
 */
@Slf4j
public class HttpsConnectHandler extends ChannelInboundHandlerAdapter {
    private static final String LOG_PRE = "[Https连接处理类]通道id:{}";

    /**
     * 与客户端连接的处理器(ProxyServerHandler)中的ctx,
     * 用于将目标主机响应的消息 发送回 客户端
     */
    private final ChannelHandlerContext ctx;

    public HttpsConnectHandler(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * 当目标服务器取消注册
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx0) throws Exception {
        log.info(LOG_PRE + ",在目标服务器取消注册", ctx0.channel().id());

        //关闭与客户端的通道
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx1, Object msg) throws Exception {
        log.info(LOG_PRE + ",读取到响应.", ctx1.channel().id());
        //使用客户端通道的ctx,将消息发回给客户端
        ctx.writeAndFlush(msg);

    }


    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx0, Throwable cause) throws Exception {
        cause.printStackTrace();
        //关闭 与目标服务器的连接
        ctx0.close();
        //关闭 与客户端的连接
        ctx.close();
    }
}
