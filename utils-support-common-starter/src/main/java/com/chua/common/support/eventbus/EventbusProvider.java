package com.chua.common.support.eventbus;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.environment.EnvironmentProvider;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.log.Log;
import com.chua.common.support.spi.ServiceFactory;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.Builder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 时间总线
 * @author CH
 */
@Builder
public class EventbusProvider implements ServiceFactory<Eventbus>, InitializingAware, AutoCloseable{

    private static final Log log = Log.getLogger(EventbusProvider.class);

    private Profile profile;

    private Executor executor;
    private final ConcurrentMap<String, Eventbus> adaptors = new ConcurrentHashMap<>();

    private final Map<Object, Set<EventbusEvent>> subscribes = new IdentityHashMap<>(16);
    public EventbusProvider() {
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() {
        EnvironmentProvider environmentProvider = new EnvironmentProvider(profile);
        Map<String, Eventbus> list = list();
        for (Map.Entry<String, Eventbus> entry : list.entrySet()) {
            Eventbus eventbus = entry.getValue();
            eventbus.executor(executor);
            environmentProvider.refresh(eventbus);
            addEventbus(eventbus.event().name(), eventbus);
        }
    }


    public EventbusProvider addEventbus(Enum eventbusType, Eventbus eventbus) {
        return addEventbus(eventbusType.name(), eventbus);
    }

    public EventbusProvider addEventbus(String eventbusType, Eventbus eventbus) {
        String name = eventbusType.toLowerCase();
        if (adaptors.containsKey(name)) {
            return this;
        }

        eventbus.executor(executor);
        adaptors.put(name, eventbus);
        return this;
    }

    @Override
    public void close() throws Exception {
        adaptors.forEach((k, v) -> {
            try {
                v.close();
            } catch (Exception ignored) {
            }
        });
    }

    public void register(Object entity) {
        Class<?> aClass = entity.getClass();

        Map<Method, Subscribe> collect = Arrays.stream(aClass.getDeclaredMethods()).filter(it -> {
            Subscribe subscribe = it.getDeclaredAnnotation(Subscribe.class);
            return null != subscribe;
        }).collect(Collectors.toMap(it -> it, it1 -> it1.getDeclaredAnnotation(Subscribe.class)));


        if(collect.isEmpty()) {
            return;
        }

        for (Map.Entry<Method, Subscribe> entry : collect.entrySet()) {
            processSubscribe(entry.getValue(), entry.getKey().getName(), entry.getKey(), entity);
        }

        finishEvent();
    }

    public void register(Method method, Object entity) {
        Subscribe subscribe1 = method.getDeclaredAnnotation(Subscribe.class);
        if (null == subscribe1) {
            return;
        }

        processSubscribe(subscribe1, method.getName(), method, entity);

        finishEvent();
    }

    public void register(ConcurrentMap<String, Eventbus> concurrentMap) {
        this.adaptors.putAll(concurrentMap);
    }

    public synchronized void post(String name, Object message) {
        post(null, name, message);
    }

    public synchronized void post(EventbusType type, String name, Object message) {
        if (null == message) {
            log.warn("消息不能为空");
            return;
        }
        List<Eventbus> adaptors = new ArrayList<>();
        if (null == type) {
            adaptors.addAll(this.adaptors.values());
        } else {
            adaptors.add(this.adaptors.get(type.name().toLowerCase()));
        }

        for (Eventbus adaptor : adaptors) {
            adaptor.post(name, message);
        }
    }

    /**
     * 预加载
     *
     * @param subscribe 注解
     * @param beanName  名称
     * @param method    方法
     * @param bean      对象
     */
    private void processSubscribe(Subscribe subscribe, String beanName, Method method, Object bean) {
        Map<String, Object> attributes = AnnotationUtils.getAnnotationValue(subscribe);
        Object value = "";
        if (null != attributes) {
            value = attributes.get("name");

            if (StringUtils.isNullOrEmpty(value.toString())) {
                value = attributes.get("value");
            }

            if (null == value) {
                value = "";
            }
        }
        subscribes.computeIfAbsent(value.toString(), it -> new HashSet<>()).add(new EventbusEvent(subscribe, method, beanName, bean));
    }

    /**
     * 完成事件
     */
    private void finishEvent() {
        Map<String, Set<EventbusEvent>> typeAndEntity = new HashMap<>(1 << 4);
        subscribes.forEach((key, value) -> {
            value.forEach(it -> {
                String eventbusType = it.getType().name().toLowerCase();
                if (adaptors.containsKey(eventbusType)) {
                    typeAndEntity.computeIfAbsent(eventbusType, it1 -> new HashSet<>()).add(it);
                }
            });
        });
        finishEvent(typeAndEntity, adaptors);
    }

    /**
     * 任务
     *
     * @param typeAndEntity 注册对象
     * @param concurrentMap 解释器
     */
    private void finishEvent(Map<String, Set<EventbusEvent>> typeAndEntity, ConcurrentMap<String, Eventbus> concurrentMap) {
        typeAndEntity.forEach((key, value) -> {
            Eventbus eventbus = concurrentMap.get(key);
            if (null != eventbus) {
                eventbus.register(value.toArray(new EventbusEvent[0]));
            }
        });
    }

    public Eventbus getEventbus(EventbusType eventbusType) {
        return adaptors.get(eventbusType.name().toLowerCase());
    }
}
