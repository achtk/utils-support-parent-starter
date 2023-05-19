package com.chua.server.support.server.proxy;

import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.net.impl.SocketAddressImpl;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * http代理
 *
 * @author CH
 */
@Slf4j
public class HttpProxyServer extends AbstractServer {

    private HttpServer httpServer;
    private Vertx vertx;
    private HttpProxy httpProxy;

    protected HttpProxyServer(ServerOption serverOption, String... args) {
        super(serverOption);
    }

    @Override
    public void run() {
        this.vertx = Vertx.vertx();
        this.httpServer = vertx.createHttpServer();
        this.httpProxy = createHttpProxy();

        HttpServer httpServer = this.httpServer.requestHandler(req -> {
            log.info("proxy request : {}", req.uri());
            httpProxy.proxy(req);
        });
        int port = getPort();
        String host = getHost();
        InetSocketAddress inetSocketAddress;
        if (StringUtils.isNullOrEmpty(host)) {
            inetSocketAddress = new InetSocketAddress(port);
        } else {
            inetSocketAddress = new InetSocketAddress(host, port);
        }
        httpServer.listen(new SocketAddressImpl(inetSocketAddress));
    }

    @Override
    public void shutdown() {
        if (null != httpServer) {
            httpServer.close();
        }

        if (null != vertx) {
            vertx.close();
        }

        try {
            httpProxy.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() {
    }

    /**
     * 创建代理
     *
     * @return 代理
     */
    private HttpProxy createHttpProxy() {
        return new HttpProxyImpl();
    }

    protected interface HttpProxy extends AutoCloseable {

        /**
         * 代理
         *
         * @param req 请求
         */
        void proxy(HttpServerRequest req);
    }

    private class HttpProxyImpl implements HttpProxy {
        private final Vertx vertx;
        private final Map<URI, HttpClient> httpClientMap = new ConcurrentHashMap<>();

        public HttpProxyImpl() {
            this.vertx = Vertx.vertx();
        }


        @Override
        public void close() throws Exception {
            vertx.close();
        }

        @Override
        public void proxy(HttpServerRequest httpServerRequest) {
            URI targetUri1 = null;
            try {
                targetUri1 = new URI(Objects.requireNonNull(request.getString("proxy-address")));
            } catch (URISyntaxException ignored) {
            }

            URI targetUri = targetUri1;
            String path = httpServerRequest.path();
            if ("/".equals(path)) {
                path = httpServerRequest.uri().substring(1);
            } else {
                path = httpServerRequest.uri();
            }

            HttpClient httpClient = httpClientMap.get(targetUri);
            String target = StringUtils.defaultString(targetUri.getPath(), "/") + path;

            HttpServerResponse httpServerResponse = httpServerRequest.response();

            httpServerRequest.bodyHandler(reqTotalBuffer -> {
                httpClient.request(httpServerRequest.method(), target)
                        .onComplete(r -> {
                            if (r.succeeded()) {
                                HttpClientRequest httpClientRequest = r.result();
                                httpClientRequest.response()
                                        .onComplete(c -> {
                                            if (c.succeeded()) {
                                                HttpClientResponse httpClientResponse = c.result();
                                                // 复制httpclientreponse 响应头给httpreponse
                                                copyHttpClientReposeHeaderToRequest(httpClientResponse, httpServerResponse);
                                                // 拿到httpclient 响应处理
                                                String uri = httpClientRequest.absoluteURI();
                                                httpClientResponse.bodyHandler(buffer -> {
                                                    byte[] bytes = ByteBufUtil.getBytes(buffer.getByteBuf());
                                                    String contentEncoding = httpClientResponse.headers().get(HttpHeaderNames.CONTENT_ENCODING);
                                                    if (!StringUtils.isNullOrEmpty(contentEncoding) && contentEncoding.equalsIgnoreCase(HttpHeaderValues.GZIP.toString())) {
                                                        // 此处判断是否开启gzip，如果是需要解压内容，然后修改
                                                    }
                                                    httpServerResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length + "");
                                                    httpServerResponse.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
                                                    httpServerResponse.end(Buffer.buffer(bytes));
                                                });
                                            } else {
                                                httpServerResponse.reset();
                                                c.cause().printStackTrace();
                                            }
                                        });
                                copyRequestHeaderToHttpClient(httpServerRequest, httpClientRequest, targetUri);
                                httpClientRequest.send(reqTotalBuffer);
                            } else {
                                httpServerResponse.reset();
                                r.cause().printStackTrace();
                            }
                        });
            });

        }


        public void copyHttpClientReposeHeaderToRequest(HttpClientResponse httpClientResponse, HttpServerResponse httpServerResponse) {
            String key;
            String value;
            for (Map.Entry<String, String> map : httpClientResponse.headers()) {
                key = map.getKey();
                value = map.getValue();
                httpServerResponse.putHeader(key, value);
            }
            httpServerResponse.setStatusCode(httpClientResponse.statusCode());
        }


        /**
         * 消息头
         *
         * @param httpServerRequest 请求
         * @param request           代理请求
         * @param targetUri         目标地址
         */
        public void copyRequestHeaderToHttpClient(HttpServerRequest httpServerRequest, HttpClientRequest request, URI targetUri) {
            String proxy = null;
            try {
                proxy = targetUri.toURL().getHost();
            } catch (MalformedURLException ignored) {
            }
            String dst = httpServerRequest.host();
            // 复制request header
            log.info("request proxy:{}->dst:{}", proxy, dst);
            String key;
            String value;
            for (Map.Entry<String, String> m : httpServerRequest.headers()) {
                key = m.getKey();
                value = m.getValue();
                if (key.equalsIgnoreCase(HttpHeaderNames.HOST.toString())) {
                    value = proxy;
                } else if (key.equalsIgnoreCase(HttpHeaderNames.REFERER.toString()) || key.equalsIgnoreCase(HttpHeaderNames.ORIGIN.toString())) {
                    value = value.replace(dst, proxy);
                }
                request.headers().add(key, value);
            }
        }
    }

}
