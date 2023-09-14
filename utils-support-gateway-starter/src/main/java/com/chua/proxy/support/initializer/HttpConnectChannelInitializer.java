package com.chua.proxy.support.initializer;

import com.chua.proxy.support.channel.HttpConnectHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * 用于客户端http请求的 通道初始化器
 *
 * @author CH
 * @since 2023/09/14
 */
public class HttpConnectChannelInitializer extends ChannelInitializer<SocketChannel> {

	/**
	 * 与客户端连接的处理器(ProxyServerHandler)中的ctx,
	 * 用于将目标主机响应的消息 发送回 客户端
	 * <p>
	 * 此处将其传给http连接对应的处理器类
	 */
	private ChannelHandlerContext ctx;

	public HttpConnectChannelInitializer(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		socketChannel.pipeline()
				//作为客户端时的请求编码解码
				.addLast(new HttpClientCodec())
				//数据聚合类,将http报文转为 FullHttpRequest和FullHttpResponse
				.addLast(new HttpObjectAggregator(65536))
				//自定义处理器
				.addLast(new HttpConnectHandler(ctx));
	}

}
