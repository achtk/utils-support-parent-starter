package com.chua.proxy.support.filter;

import com.chua.proxy.support.buffer.NettyDataBuffer;
import com.chua.proxy.support.buffer.NettyDataBufferFactory;
import com.chua.proxy.support.exchange.Exchange;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServerResponse;

import java.util.concurrent.atomic.AtomicInteger;

import static com.chua.proxy.support.constant.Constants.CLIENT_RESPONSE_ATTR;
import static com.chua.proxy.support.constant.Constants.CLIENT_RESPONSE_CONN_ATTR;

/**
 * @author CH
 */
@Slf4j
public class WebClientWriteResponseFilter implements Filter{
    private final AtomicInteger state = new AtomicInteger();

    @Override
    public Mono<Void> filter(Exchange exchange, FilterChain chain) {
        return chain.filter(exchange).doOnError(throwable -> cleanup(exchange)).then(Mono.defer(() -> {
            HttpClientResponse clientResponse = exchange.getAttribute(CLIENT_RESPONSE_ATTR);
            if (clientResponse == null) {
                return Mono.empty();
            }
            Connection connection = exchange.getAttribute(CLIENT_RESPONSE_CONN_ATTR);
            NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(connection.outbound().alloc());
            log.trace("WebClientWriteResponseFilter start");
            HttpServerResponse response = exchange.getResponse();
            Flux<NettyDataBuffer> body = connection.inbound().receive()
                    .doOnSubscribe(s -> {
                        if (this.state.compareAndSet(0, 1)) {
                            return;
                        }
                        if (this.state.get() == 2) {
                            throw new IllegalStateException(
                                    "The client response body has been released already due to cancellation.");
                        }
                    })
                    .map(byteBuf -> {
                        byteBuf.retain();
                        return bufferFactory.wrap(byteBuf);
                    });

            return response.send(Flux.from(body).map(NettyDataBufferFactory::toByteBuf)).then();
        }));
    }

    /**
     * 清理
     *
     * @param exchange 交换
     */
    private void cleanup(Exchange exchange) {
        HttpClientResponse clientResponse = exchange.getAttribute(CLIENT_RESPONSE_ATTR);
    }


}
