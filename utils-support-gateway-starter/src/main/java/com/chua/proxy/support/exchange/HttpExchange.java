package com.chua.proxy.support.exchange;

import reactor.core.publisher.Mono;

/**
 * http交换
 *
 * @author CH
 */
public interface HttpExchange {

    /**
     * handle
     * @param exchange 交换
     * @return {@link Mono}<{@link Void}>
     */
    Mono<Void> handle(Exchange exchange);

}
