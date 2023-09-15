package com.chua.proxy.support.event;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * 事件发布者impl
 *
 * @author CH
 */
@Slf4j
public class EventPublisherImpl<E extends Event> implements EventPublisher<E> {

    private final Sinks.Many<E> sink;

    private final Flux<E> eventStream;

    public EventPublisherImpl() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
        this.eventStream = this.sink.asFlux();
    }

    @Override
    public synchronized void publishEvent(E event) {
        Sinks.EmitResult emitResult = sink.tryEmitNext(event);
        if (emitResult.isFailure()) {
            log.error("Failed to publish an event, emit result is {}, event object is {}", emitResult, event);
        }
    }

    @Override
    public synchronized void registerListener(EventListener<E> listener) {
        this.eventStream.subscribe(listener::onEvent);
    }

}
