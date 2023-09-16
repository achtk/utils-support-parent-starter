package com.chua.proxy.support.factory;

import com.chua.common.support.request.WebServerRequest;
import com.chua.common.support.utils.NumberUtils;
import com.chua.proxy.support.constant.Constants;
import com.chua.proxy.support.initializer.HttpConnectChannelInitializer;
import com.chua.proxy.support.initializer.HttpsConnectChannelInitializer;
import com.chua.proxy.support.listener.HttpChannelFutureListener;
import com.chua.proxy.support.listener.HttpsChannelFutureListener;
import com.chua.proxy.support.route.Route;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 代理通道处理程序
 *
 * @author CH
 * @since 2023/09/16
 */
public class ProxyChannelFactory implements ChannelFactory {

    public ProxyChannelFactory() {
    }


    @Override
    public void handle(WebServerRequest request) {
        ChannelHandlerContext ctx = request.getChannel();
        Route route = request.getAttribute(Constants.DISCOVERY);
        if (null == route) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.NOT_FOUND));
            return;
        }

        request.headers().set("Host", route.getIp() + ":" + route.getPort());

        //用工厂类构建bootstrap,用来建立socket连接
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(8))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, NumberUtils.isZero(route.getTimeout(), 10_000));
        //如果是http请求
        if (route.isHttp()) {
            //添加监听器,当连接建立成功后,转发客户端的消息给它
            bootstrap
                    .handler(new HttpConnectChannelInitializer(ctx))
                    .connect(route.address())
                    .addListener(new HttpChannelFutureListener(request, ctx));
            return;
        }
        //如果是Https请求
        bootstrap
                .handler(new HttpsConnectChannelInitializer(ctx))
                .connect(route.address())
                .addListener(new HttpsChannelFutureListener(request, ctx));

    }
}
