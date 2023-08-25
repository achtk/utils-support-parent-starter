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
public class GuavaCacheable extends AbstractCacheable {

    public GuavaCacheable() {
    }

    public GuavaCacheable(Cache<Object, Value<Object>> cache) {
        this.cache = cache;
    }

    public GuavaCacheable(Map<String, Object> config) {
        super(config);
    }

    public GuavaCacheable(CacheConfiguration config) {
        super(config);
    }

    private Cache<Object, Value<Object>> cache;

    @Override
    public Cacheable configuration(Map<String, Object> config) {
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
            this.cache = cacheBuilder.build(new CacheLoader<Object, Value<Object>>() {
                @Override
                public Value<Object> load(Object key) throws Exception {
                    updateListener.accept(key, null);
                    return Value.of(key);
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
    public boolean exist(Object key) {
        return cache.asMap().containsKey(key);
    }

    @Override
    public Value<Object> get(Object key) {
        Value<Object> ifPresent = cache.getIfPresent(key);
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
    public Value<Object> put(Object key, Object value) {
        TimeValue<Object> value1 = ObjectUtils.isPresent(TimeValue.class, value, () -> TimeValue.of(value, Duration.ofMillis(expireAfterWrite)));
        cache.put(key, value1);
        return value1;
    }

    @Override
    public Value<Object> remove(Object key) {
        Value<Object> o = get(key);
        cache.invalidate(key);
        return o;
    }
}
