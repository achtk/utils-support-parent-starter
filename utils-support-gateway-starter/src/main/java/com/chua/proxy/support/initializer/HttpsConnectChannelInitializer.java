package com.chua.proxy.support.initializer;

import com.chua.proxy.support.channel.HttpsConnectHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;

/**
 * 用于客户端https请求的 通道初始化器
 *
 * @author CH
 * @since 2023/09/14
 */
public class HttpsConnectChannelInitializer extends ChannelInitializer<SocketChannel> {

	/**
	 * 与客户端连接的处理器(ProxyServerHandler)中的ctx,
	 * 用于将目标主机响应的消息 发送回 客户端
	 * <p>
	 * 此处将其传给http连接对应的处理器类
	 */
	private final ChannelHandlerContext ctx;

	static
	SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
	public static SslContext sslContext;

	//下面这行，直接信任自签证书
	static {
		sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
		try {
			sslContext = sslContextBuilder.build();
		} catch (SSLException ignored) {
		}

	}

	public HttpsConnectChannelInitializer(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		socketChannel.pipeline()
				.addLast("ssl", sslContext.newHandler(socketChannel.alloc()))
				//https请求无法解析,不做任何编解码操作
				//自定义处理器
				.addLast(new HttpsConnectHandler(ctx));
	}

}
