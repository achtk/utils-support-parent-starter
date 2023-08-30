package com.chua.common.support.task.cache;

import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.value.TimeValue;
import com.chua.common.support.value.Value;
import org.iq80.leveldb.DB;

import java.time.Duration;
import java.util.Map;

/**
 * leveldb
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class LevelCacheable extends AbstractCacheable {


    private HTreeMap marker;

    public LevelCacheable() {
    }

    public LevelCacheable(Map<String, Object> config) {
        super(config);
    }

    public LevelCacheable(CacheConfiguration config) {
        super(config);
    }

    private DB db;


    @Override
    public void afterPropertiesSet() {
        db = DBMaker.fileDB("data/" + dir).closeOnJvmShutdown()
                .executorEnable()
                .fileMmapEnableIfSupported()
                .fileMmapEnable()
                .transactionEnable()
                .make();
        this.marker = db.hashMap(dir).createOrOpen();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> IoUtils.closeQuietly(db)));
    }

    @Override
    public void clear() {
        marker.clear();
    }

    @Override
    public boolean exist(Object key) {
        return marker.containsKey(key);
    }

    @Override
    public Value<Object> get(Object key) {
        Value<Object> objectValue = (Value<Object>) marker.get(key);
        if (hotColdBackup) {
            marker.put(key, objectValue);
        }
        return objectValue;
    }

    @Override
    public Value<Object> put(Object key, Object value) {
        TimeValue<Object> value1 = ObjectUtils.isPresent(TimeValue.class, value, () -> TimeValue.of(value, Duration.ofMillis(expireAfterWrite)));
        marker.put(key, value1);
        return value1;
    }

    @Override
    public Value<Object> remove(Object key) {
        return (Value<Object>) marker.remove(key);
    }
}
