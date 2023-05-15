package com.chua.common.support.json.jsonpath.spi.cache;

import com.chua.common.support.json.jsonpath.JsonPathException;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static com.chua.common.support.json.jsonpath.internal.Utils.notNull;

/**
 * @author Administrator
 */
public class CacheProvider {

    private static final AtomicReferenceFieldUpdater<CacheProvider, Cache> UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(CacheProvider.class, Cache.class, "cache");
    private static final CacheProvider CACHE_PROVIDER = new CacheProvider();

    private volatile Cache cache;

    private static class CacheHolder {
        static final Cache CACHE;

        static {
            Cache cache = CacheProvider.CACHE_PROVIDER.cache;
            // the application is trying to use the cache
            // and if no external implementation has been registered,
            // we need to initialise it to the default LRUCache
            if (cache == null) {
                cache = getDefaultCache();
                // on the off chance that the cache implementation was registered during
                // initialisation of the holder, this should be respected, so if the
                // default cache can't be written back, just read the user supplied value again
                if (!UPDATER.compareAndSet(CACHE_PROVIDER, null, cache)) {
                    cache = CacheProvider.CACHE_PROVIDER.cache;
                }
            }
            CACHE = cache;
        }
    }

    public static void setCache(Cache cache) {
        notNull(cache, "Cache may not be null");
        if (!UPDATER.compareAndSet(CACHE_PROVIDER, null, cache)) {
            throw new JsonPathException("Cache provider must be configured before cache is accessed and must not be registered twice.");
        }
    }

    public static Cache getCache() {
        return CacheHolder.CACHE;
    }


    private static Cache getDefaultCache() {
        return new LRUCache(400);
        //return new NOOPCache();
    }
}
