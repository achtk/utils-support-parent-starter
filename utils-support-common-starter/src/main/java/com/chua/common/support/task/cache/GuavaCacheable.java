package com.chua.common.support.task.cache;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.value.TimeValue;
import com.chua.common.support.value.Value;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * jdk
 *
 * @author CH
 * @since 2022-04-22
 */
@Spi("guava")
@SpiDefault
public class GuavaCacheable<K, V> extends AbstractCacheable<K, V> {

    public GuavaCacheable() {
    }

    public GuavaCacheable(Cache<K, Value<V>> cache) {
        this.cache = cache;
    }

    public GuavaCacheable(Map<String, Object> config) {
        super(config);
    }

    public GuavaCacheable(CacheConfiguration config) {
        super(config);
    }

    private Cache<K, Value<V>> cache;

    @Override
    public Cacheable<K, V> configuration(Map<String, Object> config) {
        super.configuration(config);
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
                .initialCapacity(capacity < 1 ? 100000 : capacity)
                .maximumSize(maximumSize < 1 ? 100000 : maximumSize);

        if (expireAfterAccess > -1) {
            cacheBuilder.expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
        }

        if (expireAfterWrite > -1) {
            cacheBuilder.expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS);
        }

        if (refreshAfterWrite > -1) {
            cacheBuilder.refreshAfterWrite(refreshAfterWrite, TimeUnit.SECONDS);
        }

        if (null != removeListener) {
            cacheBuilder.removalListener(notification -> removeListener.accept(notification.getKey(), notification.getCause().name()));
        }

        if (state) {
            cacheBuilder.recordStats();
        }

        if (null == updateListener) {
            this.cache = cacheBuilder.build();
        } else {
            this.cache = cacheBuilder.build(new CacheLoader<K, Value<V>>() {
                @Override
                public Value<V> load(K key) throws Exception {
                    updateListener.accept(key, null);
                    return Value.of(null);
                }
            });
        }
        return this;
    }


    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public boolean exist(K key) {
        return cache.asMap().containsKey(key);
    }

    @Override
    public Value<V> get(K key) {
        Value<V> ifPresent = cache.getIfPresent(key);
        if (null == ifPresent) {
            return Value.of(null);
        }

        if (hotColdBackup) {
            cache.put(key, ifPresent);
        }

        return ifPresent;
    }


    @Override
    @SuppressWarnings("ALL")
    public Value<V> put(K key, V value) {
        TimeValue<V> value1 = ObjectUtils.isPresent(TimeValue.class, value, () -> TimeValue.of(value, Duration.ofMillis(expireAfterWrite)));
        cache.put(key, value1);
        return value1;
    }

    @Override
    public Value<V> remove(K key) {
        Value<V> o = get(key);
        cache.invalidate(key);
        return o;
    }
}
