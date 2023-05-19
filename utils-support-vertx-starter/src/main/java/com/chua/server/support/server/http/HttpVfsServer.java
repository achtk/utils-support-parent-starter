package com.chua.server.support.server.http;

import com.chua.common.support.context.bean.BeanObject;
import com.chua.common.support.context.bean.BeanObjectValue;
import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.annotations.Mapping;
import com.chua.common.support.protocol.server.request.Request;
import com.chua.common.support.protocol.server.resolver.Resolver;
import com.chua.common.support.utils.StringUtils;
import com.chua.server.support.server.parameter.VertxParameterResolver;
import com.chua.server.support.server.request.VertxRequest;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.impl.SocketAddressImpl;

import java.net.InetSocketAddress;

/**
 * http
 *
 * @author CH
 */
public class HttpVfsServer extends AbstractServer {

    final Vertx vertx = Vertx.vertx();

    protected HttpVfsServer(ServerOption serverOption, String... args) {
        super(serverOption);
    }


    @Override
    public void run() {
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(request -> {
            String uri =  StringUtils.removeSuffixContains(request.uri(), "?");;
            BeanObject beanObject = getMapping(uri);
            if (null == beanObject) {
                HttpServerResponse response = request.response();
                //设置响应头
                response.putHeader("Content-type", "text/html;charset=utf-8");
                // 响应数据
                response.end("404");
                return;
            }

            Request request1 = new VertxRequest(request, uri);

            BeanObjectValue objectValue = beanObject.invoke(parameterDescribe -> super.getValue(parameterDescribe, request1));

            Mapping mapping = objectValue.getAnnotationValue(Mapping.class);

            Resolver resolver = super.getResolver(null == mapping ? null : mapping.produces(), request.getHeader("Accept"), uri);
            byte[] resolve = resolver.resolve(objectValue.getInvoke().getValue());
            HttpServerResponse httpServerResponse = request.response();

            //设置响应头
            httpServerResponse.putHeader("Content-Type", resolver.getContentType());
            httpServerResponse.end(Buffer.buffer(resolve));

        });

        int port = getPort();
        String host = getHost();
        InetSocketAddress inetSocketAddress;
        if (StringUtils.isNullOrEmpty(host)) {
            inetSocketAddress = new InetSocketAddress(port);
        } else {
            inetSocketAddress = new InetSocketAddress(host, port);
        }
        server.listen(new SocketAddressImpl(inetSocketAddress));
    }


    @Override
    public void shutdown() {
        vertx.close();
    }

    @Override
    public void afterPropertiesSet() {
        super.register(new VertxParameterResolver());
    }


}
