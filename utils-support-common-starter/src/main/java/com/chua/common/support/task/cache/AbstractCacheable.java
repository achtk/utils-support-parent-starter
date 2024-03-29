package com.chua.common.support.task.cache;

import com.chua.common.support.utils.MapUtils;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 缓存
 *
 * @author CH
 */
public abstract class AbstractCacheable<K, V> implements Cacheable<K, V> {
    /**
     * 初始化大小
     */
    protected int capacity;
    /**
     * 上限
     */
    protected int maximumSize;
    /**
     * 访问过期时间
     */
    protected int expireAfterAccess;
    /**
     * 写入过期时间
     */
    protected int expireAfterWrite;
    /**
     * 写入刷新时间
     */
    protected int refreshAfterWrite;
    /**
     * 移除监听
     */
    protected BiConsumer<Object, Object> removeListener;
    /**
     * 更新监听
     */
    protected BiConsumer<Object, Object> updateListener;
    /**
     * 统计
     */
    protected boolean state;
    /**
     * 文件存储文件路径
     */
    protected String path;
    /**
     * ehcache缓存名称
     */
    protected String cacheName;
    protected CacheConfiguration config;
    protected Boolean hotColdBackup;
    protected String dir;

    public AbstractCacheable(){}

    public AbstractCacheable(Map<String, Object> config) {
        this.configuration(config);
    }

    public AbstractCacheable(CacheConfiguration config) {
        this.configuration(config);
    }

    @Override
    public Cacheable<K, V> configuration(Map<String, Object> config) {
        this.capacity = MapUtils.getInteger(config, "capacity", 10000);
        this.maximumSize = MapUtils.getInteger(config, "maximumSize", 10000);
        this.expireAfterAccess = MapUtils.getInteger(config, "expireAfterAccess", -1);
        this.expireAfterWrite = MapUtils.getInteger(config, "expireAfterWrite", 30000);
        this.refreshAfterWrite = MapUtils.getInteger(config, "refreshAfterWrite", -1);
        this.removeListener = MapUtils.getType(config, "removeListener", BiConsumer.class);
        this.updateListener = MapUtils.getType(config, "updateListener", BiConsumer.class);
        this.state = MapUtils.getBoolean(config, "state", false);
        this.path = MapUtils.getString(config, "path", System.getProperty("user.home") + "/cache/");
        this.cacheName = MapUtils.getString(config, "cacheName", "default");
        this.hotColdBackup = MapUtils.getBoolean(config, "hotColdBackup", true);
        this.dir = MapUtils.getString(config, "dir", "cache");
        afterPropertiesSet();
        return this;
    }
    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void destroy() {

    }
}
