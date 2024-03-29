package com.chua.proxy.support.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于将客户端发送的Tcp请求和目标主机建立连接后,
 * 处理目标主机的输入事件的处理器
 * <p>
 * 每建立一个连接,都需要创建一个该对象
 *
 * @author CH
 * @since 2023/09/14
 */
@Slf4j
public class TcpConnectHandler extends ChannelInboundHandlerAdapter {

	private static final String LOG_PRE = "[Http连接处理类]通道id:{}";

	/**
	 * 与客户端连接的处理器(ProxyServerHandler)中的ctx,
	 * 用于将目标主机响应的消息 发送回 客户端
	 */
	private final ChannelHandlerContext ctx;

	public TcpConnectHandler(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}


	/**
	 * 当目标服务器取消注册
	 */
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx0) throws Exception {
	}


	/**
	 * 读取到消息
	 * <p>
	 * 注意,从逻辑上来说,进行到这一步,客户端已经发送了它的请求报文,并且我们也收到目标服务器的响应.
	 * 那么似乎可以直接使用如下语句,在将消息发回给客户端后,关闭与客户端的连接通道.
	 * ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
	 * 但据我理解,浏览器会复用一些通道,所以最好不要关闭.
	 * (ps: 我关闭后,看直播时,无法加载出视频.... 不将它关闭,就一切正常.  并且,我之前测试过,客户端多次连接会使用相同id的channel.
	 * 也就是同一个TCP连接.)
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx0, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		byteBuf.retain();
		ctx.writeAndFlush(byteBuf);
	}


	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx0, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx0.close();
		//关闭 与客户端的连接
		ctx.close();
	}

}
