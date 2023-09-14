package com.chua.proxy.support.initializer;

import com.chua.proxy.support.channel.TcpConnectHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * 用于客户端Tcp请求的 通道初始化器
 *
 * @author CH
 * @since 2023/09/14
 */
public class TcpConnectChannelInitializer extends ChannelInitializer<SocketChannel> {

	/**
	 * 与客户端连接的处理器(ProxyServerHandler)中的ctx,
	 * 用于将目标主机响应的消息 发送回 客户端
	 * <p>
	 * 此处将其传给http连接对应的处理器类
	 */
	private ChannelHandlerContext ctx;

	public TcpConnectChannelInitializer(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		socketChannel.pipeline()
				//自定义处理器
				.addLast(new TcpConnectHandler(ctx));
	}

}
