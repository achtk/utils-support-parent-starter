package com.chua.common.support.task.lmax;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;

import java.util.Arrays;
import java.util.function.IntFunction;

/**
 * Disruptor
 * @author CH
 */
@SuppressWarnings("ALL")
public class DisruptorGroup<T> {

    private Disruptor<T> disruptor;
    private final DisruptorEventHandlerFactory<T> handlerFactory;
    private EventHandlerGroup<T> handleEventsWith;

    public DisruptorGroup(Disruptor<T> disruptor, DisruptorEventHandlerFactory<T> handlerFactory, EventHandlerGroup<T> handleEventsWith) {
        this.disruptor = disruptor;
        this.handlerFactory = handlerFactory;
        this.handleEventsWith = handleEventsWith;
    }

    public DisruptorGroup<T> handleEventsWith(String name) {
        return new DisruptorGroup<>(disruptor, handlerFactory, handleEventsWith.handleEventsWith(handlerFactory.getEventHandler(name)));
    }


    public DisruptorGroup<T> after(String... names) {
        return new DisruptorGroup(disruptor, handlerFactory, disruptor.after(Arrays.stream(names).map(it -> handlerFactory.getEventHandler(it)).toArray((IntFunction<DisruptorEventHandler[]>) value -> new DisruptorEventHandler[0])));
    }

}
