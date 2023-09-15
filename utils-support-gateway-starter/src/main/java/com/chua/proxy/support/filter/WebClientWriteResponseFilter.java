package com.chua.proxy.support.filter;

import com.chua.proxy.support.exchange.Exchange;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServerResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.chua.proxy.support.constant.Constants.CLIENT_RESPONSE_ATTR;

/**
 * @author CH
 */
@Slf4j
public class WebClientWriteResponseFilter implements Filter{
    private final AtomicReference<State> state = new AtomicReference<>(State.NEW);
    private enum State {NEW, COMMITTING, COMMITTED}

    @Override
    public Mono<Void> filter(Exchange exchange, FilterChain chain) {
        return chain.filter(exchange).doOnError(throwable -> cleanup(exchange)).then(Mono.defer(() -> {
            HttpClientResponse clientResponse = exchange.getAttribute(CLIENT_RESPONSE_ATTR);
            if (clientResponse == null) {
                return Mono.empty();
            }
            log.trace("WebClientWriteResponseFilter start");
            HttpServerResponse response = exchange.getResponse();

            return response.
            return response.writeWith(clientResponse.body(toDataBuffers()))
                    .doOnCancel(() -> cleanup(exchange));
        }));
    }
    protected Mono<Void> doCommit(Supplier<? extends Publisher<Void>> writeAction) {
        if (!this.state.compareAndSet(State.NEW, State.COMMITTING)) {
            return Mono.empty();
        }

        this.commitActions.add(() ->
                Mono.fromRunnable(() -> {
                    applyHeaders();
                    applyCookies();
                    this.state.set(State.COMMITTED);
                }));

        if (writeAction != null) {
            this.commitActions.add(writeAction);
        }

        List<? extends Publisher<Void>> actions = this.commitActions.stream()
                .map(Supplier::get).collect(Collectors.toList());

        return Flux.concat(actions).then();
    }

    /**
     * 清理
     *
     * @param exchange 交换
     */
    private void cleanup(Exchange exchange) {
        HttpClientResponse clientResponse = exchange.getAttribute(CLIENT_RESPONSE_ATTR);
        if (clientResponse != null) {
            clientResponse.bodyToMono(Void.class).subscribe();
        }
    }


}
