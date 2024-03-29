package com.chua.proxy.support.listener;

import com.chua.proxy.support.utils.ProxyUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.net.UnknownHostException;

import static com.chua.proxy.support.initializer.HttpsConnectChannelInitializer.sslContext;

/**
 * 用于https请求的 与目标主机建立连接后的监听器类
 *
 * @author CH
 * @since 2023/09/14
 */
@Slf4j
public class HttpsChannelFutureListener implements ChannelFutureListener {
    private static final String LOG_PRE = "[https连接建立监听器]通道id:{}";

    /**
     * 客户端要发送给目标主机的消息
     */
    private Object msg;

    /**
     * 通道上下文,如果与目标主机建立连接失败,返回失败响应给客户端,并关闭连接
     */
    private ChannelHandlerContext ctx;

    public HttpsChannelFutureListener(Object msg, ChannelHandlerContext ctx) {
        this.msg = msg;
        this.ctx = ctx;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        String channelId = ProxyUtils.getChannelId(ctx);
        //连接成功操作
        if (future.isSuccess()) {
            log.info(LOG_PRE + ",与目标主机建立连接成功.", channelId);
            //将客户端请求报文发送给服务端
            if (future.channel().isWritable()) {
                future.channel().writeAndFlush(getConnect(msg));
                future.channel().pipeline().addAfter("ssl", "target-ssl", sslContext.newHandler(future.channel().alloc()));
                future.channel().writeAndFlush(msg);
            } else {
                future.channel().close();
            }
            return;
        }
        log.info(LOG_PRE + ",与目标主机建立连接失败.", channelId);
        //给客户端响应连接超时信息
        ProxyUtils.responseFailedToClient(ctx);

        //日志记录
        Throwable cause = future.cause();
        if (cause instanceof ConnectTimeoutException) {
            log.error(LOG_PRE + ",连接超时:{}", channelId, cause.getMessage());
        } else if (cause instanceof UnknownHostException) {
            log.error(LOG_PRE + ",未知主机:{}", channelId, cause.getMessage());
        } else {
            log.error(LOG_PRE + ",异常:{}", channelId, cause.getMessage(), cause);
        }
        log.info(LOG_PRE + ",给客户端响应失败信息成功.", channelId);
        //关闭 与客户端的连接
//		ctx.close();
    }

    private static FullHttpRequest getConnect(Object msg) {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.CONNECT, "www.baidu.com:443", Unpooled.EMPTY_BUFFER, false);
        request.headers().set("host", ((FullHttpRequest) msg).headers().get("host"));

        return request;
    }
}
