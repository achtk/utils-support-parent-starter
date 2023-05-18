package com.chua.common.support.task.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存管理器
 *
 * @author CH
 */
@Slf4j
public class CacheManager {

    private static final Map<String, Cacheable> CACHEABLE_MAP = new ConcurrentHashMap<>();

    private static final CacheManager CACHE_MANAGER = new CacheManager();

    /**
     * 默认管理器
     *
     * @return 默认管理器
     */
    public static CacheManager getInstance() {
        return CACHE_MANAGER;
    }

    /**
     * 添加缓存
     *
     * @param name      名称
     * @param cacheable 缓存
     * @return this
     */
    public CacheManager addCacheable(String name, Cacheable cacheable) {
        if (!CACHEABLE_MAP.containsKey(name)) {
            CACHEABLE_MAP.put(name, cacheable);
        }
        return this;
    }

    /**
     * 添加缓存
     *
     * @param name 名称
     * @return this
     */
    public Cacheable getCacheable(String name) {
        if (CACHEABLE_MAP.containsKey(name)) {
            return CACHEABLE_MAP.get(name);
        }
        return null;
    }
}
