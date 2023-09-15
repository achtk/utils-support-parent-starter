
package com.chua.proxy.support.exchange;

import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 交换impl
 *
 * @author CH
 */
public class ExchangeImpl implements Exchange {

    private final HttpServerRequest request;

    private final HttpServerResponse response;

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public ExchangeImpl(HttpServerRequest request, HttpServerResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public HttpServerRequest getRequest() {
        return request;
    }

    @Override
    public HttpServerResponse getResponse() {
        return response;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Builder mutate() {
        return new DefaultExchangeBuilder(this);
    }

}
