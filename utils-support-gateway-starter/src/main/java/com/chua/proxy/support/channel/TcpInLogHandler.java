package com.chua.proxy.support.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 日志处理程序中tcp
 *
 * @author CH
 * @since 2023/09/13
 */
@Slf4j
public class TcpInLogHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private String clientIP;

    private String clientPort;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        log.debug("TcpInLogHandler收到消息：{}", msg.toString());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
        clientIP = socket.getAddress().getHostAddress();
        clientPort = String.valueOf(socket.getPort());
        log.debug("收到来自{}：{}的请求，成功创建Channel[{}]", clientIP, clientPort, ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("关闭来自{}：{}的请求，成功回收Channel[{}]", clientIP, clientPort, ctx.channel().id());
        super.channelInactive(ctx);
    }

    /**
     * 通道异常触发
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (!channel.isActive()) {
            ctx.close();
        }
        log.error("TcpProxyServer连接[{}]发生异常：{}", ctx.channel().id(), cause.getStackTrace());
    }
}
