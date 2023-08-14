package com.chua.redis.support.cache;

import com.chua.common.support.json.Json;
import com.chua.common.support.task.cache.AbstractCacheable;
import com.chua.common.support.task.cache.CacheConfiguration;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.value.Value;
import com.chua.redis.support.util.JedisUtil;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * redis
 *
 * @author CH
 */
public class RedisCacheable extends AbstractCacheable {

    private Jedis jedis;

    public RedisCacheable() {
    }

    public RedisCacheable(Jedis jedis) {
        this.jedis = jedis;
    }

    public RedisCacheable(Map<String, Object> config) {
        super(config);
    }

    public RedisCacheable(CacheConfiguration config) {
        super(config);
    }

    @Override
    public void afterPropertiesSet() {
        this.jedis = JedisUtil.getJedis(MapUtils.asProp(config));
    }

    @Override
    public void destroy() {
        jedis.close();
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean exist(Object key) {
        return jedis.exists(key.toString());
    }

    @Override
    public Value<Object> get(Object key) {
        String s = jedis.get(key.toString());
        return Json.fromJson(s, Value.class);
    }

    @Override
    public Value<Object> put(Object key, Object value) {
        Value<Object> value1 = Value.of(value);
        jedis.setex(key.toString(), expireAfterWrite, Json.toJson(value1));
        return value1;
    }

    @Override
    public Value<Object> remove(Object key) {
        jedis.del(key.toString());
        return null;
    }
}
