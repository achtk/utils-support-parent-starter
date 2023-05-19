package com.chua.guava.support.eventbus;

import com.chua.common.support.eventbus.AbstractEventbus;
import com.chua.common.support.eventbus.Eventbus;
import com.chua.common.support.eventbus.EventbusEvent;
import com.chua.common.support.eventbus.EventbusType;
import com.google.common.base.Strings;
import com.google.common.eventbus.AsyncEventBus;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.eventbus.EventbusType.GUAVA;

/**
 * @author CH
 */
public class GuavaEventBus extends AbstractEventbus {

    private final Map<String, AsyncEventBus> cache = new ConcurrentHashMap<>();

    @Override
    public Eventbus register(Set<EventbusEvent> value) {
        value.forEach(it -> {
            AsyncEventBus asyncEventBus = cache.computeIfAbsent(it.getName(), i -> new AsyncEventBus(executor));
            asyncEventBus.register(it.getBean());
        });
        return this;
    }

    @Override
    public Eventbus unregister(EventbusEvent value) {
        AsyncEventBus asyncEventBus = cache.get(value.getName());
        if (null != asyncEventBus) {
            asyncEventBus.unregister(value.getBean());
        }
        return this;
    }

    @Override
    public Eventbus post(String name, Object message) {
        if (Strings.isNullOrEmpty(name)) {
            cache.values().forEach(it -> it.post(message));
            return;
        }

        AsyncEventBus asyncEventBus = cache.get(name);
        if (null != asyncEventBus) {
            asyncEventBus.post(message);
        }
        return this;
    }

    @Override
    public EventbusType event() {
        return GUAVA;
    }
}
