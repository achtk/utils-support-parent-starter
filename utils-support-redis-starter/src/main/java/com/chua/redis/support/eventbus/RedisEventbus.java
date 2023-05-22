package com.chua.redis.support.eventbus;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.eventbus.*;
import com.chua.common.support.json.Json;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.redis.support.config.RedisConfiguration;
import com.chua.redis.support.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * redis
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/25
 */
@Spi("redis")
public class RedisEventbus extends AbstractEventbus {


    private static final AtomicBoolean IS_RUNNING = new AtomicBoolean(false);
    private final Map<String, Set<EventbusEvent>> temp = new HashMap<>();
    private final List<EventbusEvent> empty = new ArrayList<>();
    private JedisPool shardedJedisPool;

    public RedisEventbus(Profile profile) {
        super(profile);
        RedisConfiguration redisConfiguration = profile.bind(new String[]{"redis", "spring.redis"}, RedisConfiguration.class);

        this.shardedJedisPool = JedisUtil.getJedisPool(redisConfiguration);
        if (null != shardedJedisPool) {
            IS_RUNNING.set(true);
        }
    }

    @Override
    public EventbusType event() {
        return EventbusType.REDIS;
    }

    @Override
    public Eventbus register(EventbusEvent[] value) {
        if (!IS_RUNNING.get()) {
            IS_RUNNING.set(true);
        }
        for (EventbusEvent eventbusEvent : value) {
            String name = eventbusEvent.getName();
            if (StringUtils.isNullOrEmpty(name)) {
                empty.add(eventbusEvent);
                continue;
            }
            temp.computeIfAbsent(name, it -> new HashSet<>()).add(eventbusEvent);
        }

        for (Map.Entry<String, Set<EventbusEvent>> entry : temp.entrySet()) {
            executor.execute(() -> {
                while (IS_RUNNING.get()) {
                    try (Jedis resource = shardedJedisPool.getResource()) {
                        String message = null;
                        try {
                            byte[] lpop = resource.lpop(entry.getKey().getBytes(UTF_8));
                            message = new String(lpop, UTF_8);
                        } catch (Exception ignored) {
                        }

                        if (null == message) {
                            continue;
                        }

                        EventbusMessage subscribeMessage = Json.fromJson(message, EventbusMessage.class);
                        invoke(entry.getValue(), subscribeMessage);
                        invoke(empty, subscribeMessage);
                    }
                }

            });
        }
        return this;
    }

    @Override
    public Eventbus unregister(EventbusEvent value) {
        if (null == value) {
            return this;
        }
        Method method1 = value.getMethod();
        if (null == method1) {
            return this;
        }
        String name = value.getName();

        if (StringUtils.isNullOrEmpty(name)) {
            List<EventbusEvent> list = intoRemoveList(empty, value);
            if (!CollectionUtils.isEmpty(list)) {
                empty.removeAll(list);
            }
        } else {
            Set<EventbusEvent> subscribeTasks = temp.get(name);
            if (null != subscribeTasks) {
                List<EventbusEvent> list = intoRemoveList(subscribeTasks, value);
                if (!CollectionUtils.isEmpty(list)) {
                    list.forEach(subscribeTasks::remove);
                }
            }
        }
        return this;
    }

    /**
     * 获取删除的对象
     *
     * @param source 数据源
     * @param value  比较数据
     * @return 删除的对象
     */
    private List<EventbusEvent> intoRemoveList(Collection<EventbusEvent> source, EventbusEvent value) {
        List<EventbusEvent> list = new ArrayList<>();
        Method method1 = value.getMethod();
        source.forEach(it -> {
            if (it.getBean() != value.getBean()) {
                return;
            }
            Method method = it.getMethod();
            if (!method.getName().equals(method1.getName())) {
                return;
            }
            if (!ArrayUtils.isEquals(method.getParameterTypes(), method1.getParameterTypes())) {
                return;
            }
            list.add(it);
        });
        return list;
    }

    @Override
    public Eventbus post(String name, Object message) {
        if (StringUtils.isNullOrEmpty(name) || null == message || !IS_RUNNING.get() ) {
            return this;
        }

        try (Jedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.rpush(name.getBytes(UTF_8), Json.toJsonByte(new EventbusMessage(message)));
        }

        return this;
    }


    @Override
    public void close() throws Exception {
        super.close();
        IS_RUNNING.set(false);
    }
}
