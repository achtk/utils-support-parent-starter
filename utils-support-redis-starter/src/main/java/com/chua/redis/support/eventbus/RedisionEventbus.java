package com.chua.redis.support.eventbus;

import com.chua.common.support.eventbus.*;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.redis.support.config.RedisConfiguration;
import com.chua.redis.support.util.RedissonUtils;
import lombok.NoArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;

/**
 * redis
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/25
 */
@NoArgsConstructor
@Spi("redission")
public class RedisionEventbus extends AbstractEventbus {


    private static final AtomicBoolean IS_RUNNING = new AtomicBoolean(false);
    private final Map<String, Set<EventbusEvent>> temp = new HashMap<>();
    private final List<EventbusEvent> empty = new ArrayList<>();
    private RedissonClient redissonClient;

    @Override
    public EventbusType event() {
        return EventbusType.REDIS;
    }

    @Override
    public Eventbus register(Set<EventbusEvent> value) {
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
            RTopic topic = redissonClient.getTopic(entry.getKey());
            topic.addListener(EventbusMessage.class, new MessageListener<EventbusMessage>() {
                @Override
                public void onMessage(CharSequence charSequence, EventbusMessage eventbusMessage) {
                    invoke(entry.getValue(), eventbusMessage);
                    invoke(empty, eventbusMessage);
                }
            });
        }

        return this;
    }

    @Override
    public void unregister(EventbusEvent value) {
        if (null == value) {
            return;
        }
        Method method1 = value.getMethod();
        if (null == method1) {
            return;
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
    public void post(String name, Object message) {
        if (StringUtils.isNullOrEmpty(name) || null == message || !IS_RUNNING.get() || configuration.isEmpty()) {
            return;
        }

        for (String s : name.split(SYMBOL_COMMA)) {
            RTopic topic = redissonClient.getTopic(s);
            if (null == topic) {
                continue;
            }

            topic.publish(new EventbusMessage(message));
        }
    }

    @Override
    public Eventbus properties(EventbusConfiguration configuration) {
        super.properties(configuration);
        RedisConfiguration redisConfiguration = configuration.bind("redis", RedisConfiguration.class);
        this.redissonClient = RedissonUtils.create(redisConfiguration, configuration.getExecutor());
        IS_RUNNING.set(true);
        return this;
    }


    @Override
    public void close() throws Exception {
        super.close();
        IS_RUNNING.set(false);
    }
}
