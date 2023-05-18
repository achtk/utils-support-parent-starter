package com.chua.guava.support.cache;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.task.cache.AbstractCacheable;
import com.chua.common.support.task.cache.Cacheable;
import com.chua.common.support.value.Value;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * jdk
 *
 * @author CH
 * @since 2022-04-22
 */
@Spi("guava")
public class GuavaCacheable extends AbstractCacheable {

    private Cache<Object, Value<Object>> cache;

    @Override
    public Cacheable configuration(Map<String, Object> config) {
        super.configuration(config);
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
                .initialCapacity(capacity)
                .maximumSize(maximumSize);

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
    public boolean exist(String key) {
        return cache.asMap().containsKey(key);
    }

    @Override
    public Object get(String key) {
        Value<Object> ifPresent = cache.getIfPresent(key);
        if (null == ifPresent) {
            return null;
        }
        return ifPresent.getValue();
    }


    @Override
    public Object put(String key, Object value) {
        cache.put(key, Value.of(value));
        return value;
    }

    @Override
    public Object remove(String key) {
        Object o = get(key);
        cache.invalidate(key);
        return o;
    }
}
