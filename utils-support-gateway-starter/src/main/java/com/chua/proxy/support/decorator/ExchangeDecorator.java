package com.chua.proxy.support.decorator;

import com.chua.proxy.support.exchange.DefaultExchangeBuilder;
import com.chua.proxy.support.exchange.Exchange;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Map;

/**
 * 交换机装饰器
 *
 * @author CH
 */
public class ExchangeDecorator implements Exchange {

    private final Exchange delegator;

    public ExchangeDecorator(Exchange exchange) {
        this.delegator = exchange;
    }

    @Override
    public HttpServerRequest getRequest() {
        return this.delegator.getRequest();
    }

    @Override
    public HttpServerResponse getResponse() {
        return this.delegator.getResponse();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.delegator.getAttributes();
    }

    @Override
    public Builder mutate() {
        return new DefaultExchangeBuilder(this);
    }

}
