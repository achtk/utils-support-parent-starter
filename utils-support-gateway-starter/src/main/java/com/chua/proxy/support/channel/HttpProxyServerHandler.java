package com.chua.proxy.support.channel;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.net.proxy.HttpProxyChannel;
import com.chua.common.support.net.proxy.LimitChannel;
import com.chua.common.support.net.proxy.LimitFrame;
import com.chua.proxy.support.context.ProxyContext;
import com.chua.proxy.support.message.LimitMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.Map;

import static com.chua.common.support.constant.NameConstant.HTTP;
import static com.chua.proxy.support.constant.MessageConstant.LIMIT_MESSAGE;
import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * http代理服务器处理程序
 *
 * @author CH
 * @since 2023/09/13
 */
public class HttpProxyServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        //处理100 continue请求
        if (is100ContinueExpected(msg)) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));
        }

        if (isLimit(ctx, msg)) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(new LimitMessage(msg, LIMIT_MESSAGE, "401").toByteArray()));
            return;
        }
        Map<String, HttpProxyChannel> beanOfType = ProxyContext.getInstance().getBeanOfType(HttpProxyChannel.class);
        //TODO:
    }

    /**
     * 是极限
     *
     * @param ctx     ctx
     * @param request msg
     * @return boolean
     */
    private boolean isLimit(ChannelHandlerContext ctx, FullHttpRequest request) {
        LimitChannel limitChannel = ProxyContext.getInstance().getBean(HTTP, LimitChannel.class);
        if (null == limitChannel) {
            return false;
        }

        LimitFrame frame = new LimitFrame();
        frame.setMethod(request.method().name());
        frame.setUri(request.uri());
        HttpHeaders headers = request.headers();
        frame.setHeader(ImmutableBuilder.<String, String>builderOfMap()
                .put(headers)
                .build()
        );
        return limitChannel.tryAcquire(frame);
    }
}
