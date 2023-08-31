package com.chua.common.support.task.cache;

import com.chua.common.support.extra.quickio.api.Kv;
import com.chua.common.support.extra.quickio.core.Quick;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.value.TimeValue;
import com.chua.common.support.value.Value;

import java.time.Duration;
import java.util.Map;

/**
 * leveldb
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class LevelCacheable extends AbstractCacheable {


    public LevelCacheable() {
    }

    public LevelCacheable(Map<String, Object> config) {
        super(config);
    }

    public LevelCacheable(CacheConfiguration config) {
        super(config);
    }

    private Kv using;


    @Override
    public void afterPropertiesSet() {
        using = Quick.usingKv("data/" + dir);
    }

    @Override
    public void clear() {
        using.destroy();
        afterPropertiesSet();
    }

    @Override
    public boolean exist(Object key) {
        return using.contains(key);
    }

    @Override
    public Value<Object> get(Object key) {
        Value<Object> objectValue = (Value<Object>) using.read(key, Value.class);
        if (hotColdBackup) {
            using.write(key, objectValue);
        }
        return objectValue;
    }

    @Override
    public Value<Object> put(Object key, Object value) {
        TimeValue<Object> value1 = ObjectUtils.isPresent(TimeValue.class, value, () -> TimeValue.of(value, Duration.ofMillis(expireAfterWrite)));
        using.write(key, value1);
        return value1;
    }

    @Override
    public Value<Object> remove(Object key) {
        Value<Object> objectValue = (Value<Object>) using.read(key, Value.class);
         using.erase(key);
         return objectValue;
    }
}
