package com.chua.proxy.support.filter;

import com.chua.common.support.http.HttpStatus;
import com.chua.common.support.utils.StringUtils;
import com.chua.proxy.support.buffer.DataBuffer;
import com.chua.proxy.support.buffer.NettyDataBuffer;
import com.chua.proxy.support.buffer.NettyDataBufferFactory;
import com.chua.proxy.support.client.ReactorClient;
import com.chua.proxy.support.exchange.Exchange;
import com.chua.proxy.support.global.GlobalConfig;
import com.chua.proxy.support.route.Route;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientForm;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.chua.common.support.http.HttpHeaders.*;
import static com.chua.proxy.support.constant.Constants.CLIENT_RESPONSE_ATTR;
import static com.chua.proxy.support.constant.Constants.CLIENT_RESPONSE_CONN_ATTR;

/**
 * 代理
 * @author CH
 */
@Slf4j
public class HttpClientProxyFilter implements Filter{
    private final Route route;

    private final ReactorClient reactorClient = new ReactorClient(GlobalConfig.INSTANCE.getProperties(), GlobalConfig.INSTANCE.getSslConfigurer());
    public HttpClientProxyFilter(Route route) {
        this.route = route;
    }

    @Override
    public Mono<Void> filter(Exchange exchange, FilterChain chain) {
        HttpServerRequest request = exchange.getRequest();
        NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(exchange.getResponse().alloc());

        HttpClient httpClient = getHttpClient(route, exchange);
        final HttpMethod method = request.method();
        final String url = route.getPath();

        HttpHeaders headers = request.requestHeaders();
        Flux<HttpClientResponse> responseFlux = httpClient.headers(new Consumer<HttpHeaders>() {
            @Override
            public void accept(HttpHeaders entries) {
                entries.add(headers);
                // Will either be set below, or later by Netty
                entries.remove(HOST);
//                if (preserveHost) {
//                    String host = request.getHeaders().getFirst(HttpHeaders.HOST);
//                    headers.add(HttpHeaders.HOST, host);
//                }
            }
        }).request(method).uri(url).send((req, nettyOutbound) -> {
            if (log.isTraceEnabled()) {
                nettyOutbound.withConnection(connection -> log.trace("outbound route: "
                        + connection.channel().id().asShortText() + ", inbound: " + exchange.getRequest().uri()));
            }
            return nettyOutbound.send(request.receive().retain().map(bufferFactory::wrap).map(this::getByteBuf));
        }).responseConnection((res, connection) -> {
            exchange.getAttributes().put(CLIENT_RESPONSE_ATTR, res);
            exchange.getAttributes().put(CLIENT_RESPONSE_CONN_ATTR, connection);

            HttpServerResponse response = exchange.getResponse();
            // put headers and status so filters can modify the response
            HttpHeaders headers1 = new DefaultHttpHeaders();

            res.responseHeaders().forEach(entry -> headers1.add(entry.getKey(), entry.getValue()));

            String contentTypeValue = headers1.get(CONTENT_TYPE);
            if (StringUtils.isNotEmpty(contentTypeValue)) {
                exchange.getAttributes().put("original_response_content_type", contentTypeValue);
            }
            setResponseStatus(res, response);

            response.responseHeaders().set(headers1);
            if (!headers.contains(TRANSFER_ENCODING) && headers.contains(CONTENT_LENGTH)) {
                response.responseHeaders().remove(TRANSFER_ENCODING);
            }

            return Mono.just(res);
        });

        Integer timeout = route.getTimeout();
        if (timeout != null && timeout > 0) {
            Duration responseTimeout = Duration.ofSeconds(timeout);
            responseFlux = responseFlux
                    .timeout(responseTimeout,
                            Mono.error(new TimeoutException("响应超时: " + responseTimeout)))
                    .onErrorMap(TimeoutException.class, new Function<TimeoutException, Throwable>() {
                        @Override
                        public Throwable apply(TimeoutException e) {
                            return new TimeoutException("网关超时");
                        }
                    });
        }

        return responseFlux.then(chain.filter(exchange));
    }

    private void setResponseStatus(HttpClientResponse clientResponse, HttpServerResponse response) {
        HttpStatus status = HttpStatus.resolve(clientResponse.status().code());
//        if (status != null) {
//            response.setStatusCode(status);
//        }
//        else {
//            while (response instanceof ServerHttpResponseDecorator) {
//                response = ((ServerHttpResponseDecorator) response).getDelegate();
//            }
//            if (response instanceof AbstractServerHttpResponse) {
//                ((AbstractServerHttpResponse) response).setRawStatusCode(clientResponse.status().code());
//            }
//            else {
//                // TODO: log warning here, not throw error?
//                throw new IllegalStateException("Unable to set status code " + clientResponse.status().code()
//                        + " on response of type " + response.getClass().getName());
//            }
//        }
    }

    protected ByteBuf getByteBuf(DataBuffer dataBuffer) {
        if (dataBuffer instanceof NettyDataBuffer) {
            NettyDataBuffer buffer = (NettyDataBuffer) dataBuffer;
            return buffer.getNativeBuffer();
        }
        throw new IllegalArgumentException("Unable to handle DataBuffer of type " + dataBuffer.getClass());
    }
    /**
     * 获取http客户端
     *
     * @param route    路线
     * @param exchange 交换
     * @return {@link HttpClient}
     */
    protected HttpClient getHttpClient(Route route, Exchange exchange) {
        HttpClient httpClient = reactorClient.createInstance();
        Integer timeout = route.getTimeout();
        if (timeout != null && timeout > 0) {
            return httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, route.getTimeout() * 1000);
        }
        return httpClient;
    }
    /**
     * 添加参数
     *
     * @param httpClientForm http客户请求
     * @param request           请求
     */
    private void addParam(HttpClientForm httpClientForm, HttpServerRequest request) {
        Map<String, String> params = request.params();
        if(null == params) {
            return;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            httpClientForm.attr(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 添加表单
     *
     * @param httpClientForm http客户端表单
     * @param request        请求
     */
    private void addForm(HttpClientForm httpClientForm, HttpServerRequest request) {
        if(!request.isFormUrlencoded() ) {
            return;
        }
        httpClientForm.multipart(request.isMultipart());

    }

    private void addTimeout(HttpClientRequest httpClientRequest) {
        httpClientRequest.responseTimeout(Duration.of(route.getTimeout(), ChronoUnit.SECONDS));
    }

    /**
     * 添加cookie
     *
     * @param httpClientRequest http客户请求
     * @param request           请求
     */
    private void addCookie(HttpClientRequest httpClientRequest, HttpServerRequest request) {
        for (Set<Cookie> cookies : request.cookies().values()) {
            cookies.forEach(httpClientRequest::addCookie);
        }
    }

    /**
     * 添加消息头
     *
     * @param httpClientRequest http客户请求
     * @param request           请求
     */
    private void addHeader(HttpClientRequest httpClientRequest, HttpServerRequest request) {
        httpClientRequest.headers(request.requestHeaders());

        Map<String, String> headers = route.getHeaders();
        if(null != headers) {
            headers.forEach(httpClientRequest::addHeader);
        }
    }
}
