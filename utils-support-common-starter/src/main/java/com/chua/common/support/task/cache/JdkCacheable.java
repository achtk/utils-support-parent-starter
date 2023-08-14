package com.chua.common.support.task.cache;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.value.TimeValue;
import com.chua.common.support.value.Value;

import java.time.Duration;
import java.util.Map;

/**
 * jdk
 *
 * @author CH
 */
@Spi("juc")
public class JdkCacheable extends AbstractCacheable {

    private final Map<Object, TimeValue<Object>> CACHE = new ConcurrentReferenceHashMap<>(512);

    @Override
    public void clear() {
        CACHE.clear();
    }

    @Override
    public boolean exist(Object key) {
        return CACHE.containsKey(key);
    }

    @Override
    public Value<Object> get(Object key) {
        return CACHE.get(key);
    }

    @Override
    public Value<Object> put(Object key, Object value) {
        return CACHE.put(key, TimeValue.of(value, Duration.ofMillis(expireAfterWrite)));
    }

    @Override
    public Value<Object> remove(Object key) {
        return CACHE.remove(key);
    }
}
