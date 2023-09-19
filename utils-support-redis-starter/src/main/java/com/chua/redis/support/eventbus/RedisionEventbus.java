package com.chua.redis.support.eventbus;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.eventbus.*;
import com.chua.common.support.lang.loader.BaseLazyLoader;
import com.chua.common.support.lang.loader.Loader;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.redis.support.config.RedisConfiguration;
import com.chua.redis.support.util.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static com.chua.common.support.eventbus.EventbusType.REDIS;

/**
 * redis
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/25
 */
@Slf4j
@Spi(value = "redis", order = -1)
public class RedisionEventbus extends AbstractEventbus {


    private static final AtomicBoolean IS_RUNNING = new AtomicBoolean(false);
    private final Map<String, Set<EventbusEvent>> temp = new HashMap<>();
    private final List<EventbusEvent> empty = new ArrayList<>();
    private final RedisConfiguration redisConfiguration;

    private Loader<RedissonClient> loader = new BaseLazyLoader<RedissonClient>() {
        @Override
        public RedissonClient init() {
            try {
                return RedissonUtils.create(redisConfiguration, executor);
            } catch (Exception ignored) {
                log.warn("redis消息总线启动失败");
                return null;
            }
        }

    };

    public RedisionEventbus(RedisConfiguration redisConfiguration) {
        this.redisConfiguration = redisConfiguration;
    }
    public RedisionEventbus(String host, int port, String user, String passwd) {
        this(new RedisConfiguration(host, port, user, passwd));
    }
    public RedisionEventbus() {
        this(new RedisConfiguration());
    }
    public RedisionEventbus(String host, int port) {
        this(new RedisConfiguration(host, port));
    }
    public RedisionEventbus(int port) {
        this(new RedisConfiguration(port));
    }
    public RedisionEventbus(String host) {
        this(new RedisConfiguration(host));
    }


    @Override
    public EventbusType event() {
        return REDIS;
    }

    @Override
    public SubscribeEventbus register(EventbusEvent[] value) {

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

        if (null == loader.get()) {
            return this;
        }

        for (Map.Entry<String, Set<EventbusEvent>> entry : temp.entrySet()) {
            RTopic topic = loader.get().getTopic(entry.getKey());
            topic.addListener(EventbusMessage.class, (charSequence, eventbusMessage) -> {
                invoke(entry.getValue(), eventbusMessage);
                invoke(empty, eventbusMessage);
            });
        }

        return this;
    }

    @Override
    public SubscribeEventbus unregister(EventbusEvent value) {
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
    public SubscribeEventbus post(String name, Object message) {
        if (StringUtils.isNullOrEmpty(name) || null == message || !IS_RUNNING.get()) {
            return this;
        }

        if (null == loader.get()) {
            return this;
        }

        for (String s : name.split(SYMBOL_COMMA)) {
            RTopic topic = loader.get().getTopic(s);
            if (null == topic) {
                continue;
            }

            topic.publish(new EventbusMessage(message));
        }
        return this;
    }


    @Override
    public void close() throws Exception {
        super.close();
        IS_RUNNING.set(false);
        loader.get().shutdown();
    }
}
