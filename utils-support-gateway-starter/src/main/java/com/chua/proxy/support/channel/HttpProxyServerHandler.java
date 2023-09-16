package com.chua.proxy.support.channel;

import com.chua.common.support.request.WebServerRequest;
import com.chua.common.support.utils.StringUtils;
import com.chua.proxy.support.factory.ChannelFactory;
import com.chua.proxy.support.factory.ChannelFactoryChain;
import com.chua.proxy.support.filter.Filter;
import com.chua.proxy.support.request.NettyWebServerRequest;
import com.chua.proxy.support.utils.ProxyUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.chua.proxy.support.channel.DispatcherHandler.*;

/**
 * http代理服务器处理程序
 *
 * @author CH
 * @since 2023/09/13
 */
@Slf4j
public class HttpProxyServerHandler extends ChannelInboundHandlerAdapter {

    private final List<ChannelFactory> channelFactoryList;
    private final Filter[] filter;

    public HttpProxyServerHandler(List<ChannelFactory> channelFactoryList, Filter[] filter) {
        this.channelFactoryList = channelFactoryList;
        this.filter = filter;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //HTTP/HTTPS : 如果是 http报文格式的,此时已经被编码解码器转为了该类,
        if (msg instanceof FullHttpRequest) {
            doHttpChannel(ctx, (FullHttpRequest) msg);
            return;
        }
    }


    /**
     * do http通道
     *
     * @param ctx ctx
     * @param msg 消息
     */
    private void doHttpChannel(ChannelHandlerContext ctx, FullHttpRequest msg) {
        //HTTPS :
        if (HttpMethod.CONNECT.equals(msg.method())) {
            log.info("https请求.目标:{}", msg.uri());
            //此处将 该通道 的用于报文编码解码的处理器去除,因为后续发送的https报文都是加密过的,不符合一般报文格式,我们直接转发即可
            ctx.pipeline().remove(HTTP_REQUEST_DECODER);
            ctx.pipeline().remove(HTTP_RESPONSE_ENCODER);
            ctx.pipeline().remove(HTTP_OBJECT);

            //给客户端响应成功信息 HTTP/1.1 200 Connection Established  .如果失败时关闭客户端通道 - 该方法是自己封装的
            if (!ProxyUtils.writeAndFlush(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK), true)) {
                return;
            }

            //直接退出等待下一次双方连接即可.
            return;
        }

//        if (isLimit(ctx, msg)) {
//            ctx.writeAndFlush(new DefaultFullHttpResponse(
//                    HttpVersion.HTTP_1_1,
//                    HttpResponseStatus.TOO_MANY_REQUESTS));
//            return;
//        }

        HttpHeaders headers = msg.headers();
        String s = headers.get("Proxy-Connection");
        if (StringUtils.isNotEmpty(s)) {
            headers.set("Connection", s);
            headers.remove("Proxy-Connection");
        }
        WebServerRequest request = new NettyWebServerRequest(ctx, msg);
        ChannelFactoryChain channelFactoryChain = new ChannelFactoryChain.ChannelFactoryChainImpl(channelFactoryList);
        channelFactoryChain.doChain(request);

//        HttpProxyChannel channel = ServiceProvider.of(HttpProxyChannel.class).getNewExtension("http-proxy", ctx,
//                ProxyContext.getInstance().getBean(ProxyContext.getInstance().getMappingConfig().getName(), MappingResolver.class));
//        channel.proxy(msg);
    }

    /**
     * limit
     *
     * @param ctx     ctx
     * @param request msg
     * @return boolean
     */
    private boolean isLimit(ChannelHandlerContext ctx, FullHttpRequest request) {
//        LimitConfig limitConfig = ProxyContext.getInstance().getLimitConfig();
//        LimitChannel limitChannel = ServiceProvider.of(LimitChannel.class).getNewExtension(limitConfig.getName(), limitConfig);
//        if (null == limitChannel) {
//            return false;
//        }
//        return limitChannel.tryAcquire(FrameUtils.createFrame(request));
        return false;
    }
}
