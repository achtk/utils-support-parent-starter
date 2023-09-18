package com.chua.common.support.eventbus;

import com.chua.common.support.annotations.Spi;
import com.google.common.base.Strings;
import com.google.common.eventbus.AsyncEventBus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.eventbus.EventbusType.GUAVA;

/**
 * @author CH
 */
@Spi("guava")
@SuppressWarnings("ALL")
public class GuavaSubscribeEventBus extends AbstractEventbus {

    private final Map<String, AsyncEventBus> cache = new ConcurrentHashMap<>();

    @Override
    public SubscribeEventbus register(EventbusEvent[] value) {
        for (EventbusEvent it : value) {
            AsyncEventBus asyncEventBus = cache.computeIfAbsent(it.getName(), i -> new AsyncEventBus(executor));
            asyncEventBus.register(it.getBean());
        }
        return this;
    }

    @Override
    public SubscribeEventbus unregister(EventbusEvent value) {
        AsyncEventBus asyncEventBus = cache.get(value.getName());
        if (null != asyncEventBus) {
            asyncEventBus.unregister(value.getBean());
        }
        return this;
    }

    @Override
    public SubscribeEventbus post(String name, Object message) {
        if (Strings.isNullOrEmpty(name)) {
            cache.values().forEach(it -> it.post(message));
            return this;
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
