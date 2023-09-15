package com.chua.proxy.support.filter;

import com.chua.proxy.support.exchange.Exchange;
import reactor.core.publisher.Mono;

/**
 * 过滤器链条
 *
 * @author CH
 */
public interface FilterChain {

    /**
     * 滤器
     *
     * @param exchange 交换
     * @return {@link Mono}<{@link Void}>
     */
    Mono<Void> filter(Exchange exchange);

}
