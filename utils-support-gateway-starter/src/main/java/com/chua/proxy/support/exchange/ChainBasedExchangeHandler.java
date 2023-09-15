package com.chua.proxy.support.exchange;

import com.chua.proxy.support.filter.Filter;
import com.chua.proxy.support.filter.FilterChain;
import com.chua.proxy.support.filter.FilterChainImpl;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * 基于链交换处理程序
 *
 * @author CH
 */
public class ChainBasedExchangeHandler implements HttpExchange {

    private final FilterChain filterChain;

    public ChainBasedExchangeHandler(List<Filter> filters) {
        this.filterChain = new FilterChainImpl(filters);
    }

    @Override
    public Mono<Void> handle(Exchange exchange) {
        return filterChain.filter(exchange);
    }

}
