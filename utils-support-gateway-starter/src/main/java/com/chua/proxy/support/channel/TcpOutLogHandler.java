package com.chua.proxy.support.channel;

import com.chua.common.support.utils.ByteUtils;
import com.chua.proxy.support.utils.BufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志处理程序中tcp
 *
 * @author CH
 * @since 2023/09/13
 */
@Slf4j
public class TcpOutLogHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.debug("ProxyServer发送消息：{}", ByteUtils.toHexString(BufUtils.getArray((ByteBuf) msg)));
        super.write(ctx, msg, promise);
    }
}
