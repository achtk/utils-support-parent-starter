package com.chua.proxy.support.filter;

import com.chua.proxy.support.exchange.Exchange;
import reactor.core.publisher.Mono;

/**
 * 滤器
 *
 * @author CH
 */
public interface Filter {

    /**
     * 滤器
     *
     * @param exchange 交换
     * @param chain    链条
     * @return {@link Mono}<{@link Void}>
     */
    Mono<Void> filter(Exchange exchange, FilterChain chain);

}
