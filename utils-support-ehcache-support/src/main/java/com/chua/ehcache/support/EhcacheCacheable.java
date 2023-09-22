package com.chua.ehcache.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.json.Json;
import com.chua.common.support.task.cache.AbstractCacheable;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.TimeValue;
import com.chua.common.support.value.Value;
import com.github.luben.zstd.Zstd;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.core.EhcacheManager;
import org.ehcache.xml.XmlConfiguration;

import java.time.Duration;

/**
 * @author CH
 */
@Spi("ehcache")
public class EhcacheCacheable<K, V> extends AbstractCacheable<K, V> {

    private Cache<String, String> cache;

    @Override
    @SuppressWarnings("ALL")
    public void afterPropertiesSet() {
        Configuration configuration = new XmlConfiguration(
                EhcacheCacheable.class.getResource("module-ehcache.xml")
        );
        CacheManager cacheManager = new EhcacheManager(configuration);
        this.cache = cacheManager.getCache("default", String.class, String.class);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public boolean exist(K key) {
        return cache.get(key.toString()) != null;
    }

    @Override
    public Value<V> get(K key) {
        String s = cache.get(key.toString());
        byte[] bytes = StringUtils.utf8Bytes(s);
        byte[] decompress = Zstd.decompress(bytes, bytes.length);
        return Json.fromJson(decompress, TimeValue.class);
    }

    @Override
    public Value<V> put(K key, V value) {
        TimeValue<V> timeValue = ObjectUtils.isPresent(TimeValue.class, value, () -> TimeValue.of(value, Duration.ofMillis(expireAfterWrite)));
        byte[] compress = Zstd.compress(StringUtils.utf8Bytes(Json.toJson(timeValue)));
        cache.put(key.toString(), StringUtils.utf8Str(compress));
        return timeValue;
    }

    @Override
    public Value<V> remove(K key) {
        Value<V> vValue = get(key);
        cache.remove(key.toString());
        return vValue;
    }
}
