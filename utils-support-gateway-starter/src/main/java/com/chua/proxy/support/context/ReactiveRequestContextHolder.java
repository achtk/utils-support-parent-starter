package com.chua.proxy.support.context;

import com.chua.proxy.support.exchange.Exchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.util.context.Context;


/**
 * 反应请求上下文持有人
 *
 * @author CH
 */
public class ReactiveRequestContextHolder {

    private static final String REQUEST_CONTEXT_KEY = "REQUEST_CONTEXT_KEY";

    private ReactiveRequestContextHolder() {
    }

    public static Mono<RequestContext> getContext() {
        return Mono.deferContextual(contextView -> Mono.just(Context.of(contextView)))
                .filter(ReactiveRequestContextHolder::hasRequestContext)
                .flatMap(ReactiveRequestContextHolder::getRequestContext);
    }

    private static boolean hasRequestContext(Context context) {
        return context.hasKey(REQUEST_CONTEXT_KEY);
    }

    private static Mono<RequestContext> getRequestContext(Context context) {
        return context.get(REQUEST_CONTEXT_KEY);
    }

    public static Context withRequestContext(Mono<? extends RequestContext> requestContext) {
        return Context.of(REQUEST_CONTEXT_KEY, requestContext);
    }

    public static Mono<Exchange> getExchange() {
        return getContext().map(RequestContext::getExchange);
    }

    public static Mono<HttpServerRequest> getRequest() {
        return getExchange().map(Exchange::getRequest);
    }

}
