package com.chua.common.support.task.cache;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.utils.ObjectUtils;
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
public class JdkCacheable<K, V> extends AbstractCacheable<K, V> {

    public JdkCacheable() {
    }

    public JdkCacheable(Map<String, Object> config) {
        super(config);
    }

    public JdkCacheable(CacheConfiguration config) {
        super(config);
    }

    private final Map<K, TimeValue<V>> CACHE = new ConcurrentReferenceHashMap<>(512);

    @Override
    public void clear() {
        CACHE.clear();
    }

    @Override
    public boolean exist(K key) {
        return CACHE.containsKey(key);
    }

    @Override
    public Value<V> get(K key) {
        TimeValue<V> value = CACHE.get(key);
        if (hotColdBackup) {
            value.refresh();
        }
        return value;
    }

    @Override
    @SuppressWarnings("ALL")
    public Value<V> put(K key, V value) {
        TimeValue<V> timeValue = ObjectUtils.isPresent(TimeValue.class, value, () -> TimeValue.of(value, Duration.ofMillis(expireAfterWrite)));
        CACHE.put(key, timeValue);
        return timeValue;
    }

    @Override
    public Value<V> remove(K key) {
        return CACHE.remove(key);
    }
}
