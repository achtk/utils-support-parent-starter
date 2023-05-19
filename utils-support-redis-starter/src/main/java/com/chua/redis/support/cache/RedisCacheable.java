package com.chua.redis.support.cache;

import com.chua.common.support.json.Json;
import com.chua.common.support.task.cache.AbstractCacheable;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.value.Value;
import com.chua.redis.support.util.JedisUtil;
import redis.clients.jedis.Jedis;

/**
 * redis
 *
 * @author CH
 */
public class RedisCacheable extends AbstractCacheable {

    private Jedis jedis;

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
    public boolean exist(String key) {
        return jedis.exists(key);
    }

    @Override
    public Object get(String key) {
        String s = jedis.get(key);
        return Json.fromJson(s, Value.class).getValue();
    }

    @Override
    public Object put(String key, Object value) {
        jedis.setex(key, expireAfterWrite, Json.toJson(Value.of(value)));
        return null;
    }

    @Override
    public Object remove(String key) {
        jedis.del(key);
        return null;
    }
}
