package com.chua.proxy.support.exchange;

import com.chua.proxy.support.decorator.ExchangeDecorator;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Objects;


/**
 * 默认交易所建设者
 *
 * @author CH
 */
public class DefaultExchangeBuilder implements Exchange.Builder {

    private final Exchange delegator;

    private HttpServerRequest request;

    private HttpServerResponse response;

    public DefaultExchangeBuilder(Exchange exchange) {
        this.delegator = exchange;
    }

    @Override
    public Exchange.Builder request(HttpServerRequest request) {
        this.request = request;
        return this;
    }

    @Override
    public Exchange.Builder response(HttpServerResponse response) {
        this.response = response;
        return this;
    }

    @Override
    public Exchange build() {
        return new MutativeDecorator(delegator).withRequest(request).withResponse(response);
    }

    /**
     * 变异装饰器
     *
     * @author CH
     */
    private static class MutativeDecorator extends ExchangeDecorator {

        private HttpServerRequest request;

        private HttpServerResponse response;

        private MutativeDecorator(Exchange delegator) {
            super(delegator);
        }

        private MutativeDecorator withRequest(HttpServerRequest request) {
            this.request = request;
            return this;
        }

        private MutativeDecorator withResponse(HttpServerResponse response) {
            this.response = response;
            return this;
        }

        @Override
        public HttpServerRequest getRequest() {
            return Objects.nonNull(request) ? request : super.getRequest();
        }

        @Override
        public HttpServerResponse getResponse() {
            return Objects.nonNull(response) ? response : super.getResponse();
        }

    }

}
