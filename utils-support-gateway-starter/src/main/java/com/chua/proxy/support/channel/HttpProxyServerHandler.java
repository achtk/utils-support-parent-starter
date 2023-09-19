package com.chua.proxy.support.channel;

import com.chua.proxy.support.factory.ChannelFactory;
import com.chua.proxy.support.filter.Filter;
import com.chua.proxy.support.handler.HttpHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

    private static final HttpHandler HTTP_HANDLER = new HttpHandler();

    public HttpProxyServerHandler(List<ChannelFactory> channelFactoryList, Filter[] filter) {
        this.channelFactoryList = channelFactoryList;
        this.filter = filter;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //HTTP/HTTPS : 如果是 http报文格式的,此时已经被编码解码器转为了该类,
        if (msg instanceof FullHttpRequest) {
            HTTP_HANDLER.doHttpChannel(ctx, (FullHttpRequest) msg, channelFactoryList);
            return;
        }

        if (msg instanceof WebSocketFrame) {
            doWsChannel(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * do ws频道
     *
     * @param ctx ctx
     * @param msg 消息
     */
    private void doWsChannel(ChannelHandlerContext ctx, WebSocketFrame msg) {
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
