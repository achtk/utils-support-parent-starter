package com.chua.common.support.task.cache;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.value.TimeValue;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jdk
 *
 * @author CH
 */
@Spi("juc")
public class JdkCacheable extends AbstractCacheable {

    private final Map<String, TimeValue<Object>> CACHE = new ConcurrentReferenceHashMap<>(512);

    @Override
    public void clear() {
        CACHE.clear();
    }

    @Override
    public boolean exist(String key) {
        return CACHE.containsKey(key);
    }

    @Override
    public Object get(String key) {
        return CACHE.get(key).getValue();
    }

    @Override
    public Object put(String key, Object value) {
        return CACHE.put(key, TimeValue.of(value, Duration.ofMillis(expireAfterWrite)));
    }

    @Override
    public Object remove(String key) {
        return CACHE.remove(key);
    }
}
