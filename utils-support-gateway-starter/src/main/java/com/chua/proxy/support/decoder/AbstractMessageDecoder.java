package com.chua.proxy.support.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 调度员处理程序
 *
 * @author CH
 * @since 2023/09/13
 */
@Slf4j
public abstract class AbstractMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // Will use the first five bytes to detect a protocol.
        if (byteBuf.readableBytes() < 5) {
            return;
        }
        final int magic1 = byteBuf.getUnsignedByte(byteBuf.readerIndex());
        final int magic2 = byteBuf.getUnsignedByte(byteBuf.readerIndex() + 1);

        // 判断是不是HTTP请求
        if (isHttp(magic1, magic2)) {
            log.info("this is a http msg");
            addHttpHandler(channelHandlerContext);
        } else {
            log.info("this is a socket msg");
            addTcpHandler(channelHandlerContext);
        }
        channelHandlerContext.pipeline().remove(this);
    }


    /**
     * 外部请求与ProxyServer激活channel连接时，通知ProxyClient与被代理服务预建立连接
     *
     * @param ctx ctx
     * @throws Exception 例外
     */
    public abstract void fullyConnect(ChannelHandlerContext ctx) throws Exception;

    /**
     * 配置http请求的pipeline
     *
     * @param ctx ctx
     */
    public abstract void addHttpHandler(ChannelHandlerContext ctx);

    /**
     * 配置Tcp请求的pipeline
     *
     * @param ctx ctx
     */
    public abstract void addTcpHandler(ChannelHandlerContext ctx);

    /**
     * 判断请求是否是HTTP请求
     *
     * @param magic1 报文第一个字节
     * @param magic2 报文第二个字节
     * @return boolean
     */
    public boolean isHttp(int magic1, int magic2) {
        // GET
        return magic1 == 'G' && magic2 == 'E' ||
                // POST
                magic1 == 'P' && magic2 == 'O' ||
                // PUT
                magic1 == 'P' && magic2 == 'U' ||
                // HEAD
                magic1 == 'H' && magic2 == 'E' ||
                // OPTIONS
                magic1 == 'O' && magic2 == 'P' ||
                // PATCH
                magic1 == 'P' && magic2 == 'A' ||
                // DELETE
                magic1 == 'D' && magic2 == 'E' ||
                // TRACE
                magic1 == 'T' && magic2 == 'R' ||
                // CONNECT
                magic1 == 'C' && magic2 == 'O';
    }
}
