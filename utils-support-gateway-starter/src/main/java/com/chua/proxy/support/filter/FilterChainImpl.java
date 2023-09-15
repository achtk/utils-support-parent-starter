package com.chua.proxy.support.filter;

import com.chua.proxy.support.exchange.Exchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 滤波器链impl
 *
 * @author CH
 */
public class FilterChainImpl implements FilterChain {

    private int index;

    private final List<Filter> filters;

    public FilterChainImpl(List<Filter> filters) {
        this.filters = filters;
        this.index = 0;
    }

    @Override
    public Mono<Void> filter(Exchange exchange) {
        return Mono.defer(() -> {
            if (this.index < filters.size()) {
                Filter filter = filters.get(this.index);
                this.index++;
                return filter.filter(exchange, this);
            } else {
                return Mono.empty();
            }
        });
    }

}
