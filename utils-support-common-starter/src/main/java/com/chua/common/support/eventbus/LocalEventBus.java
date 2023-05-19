package com.chua.common.support.eventbus;

import com.chua.common.support.collection.ImmutableCollection;
import com.chua.common.support.collection.Table;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.eventbus.EventbusType.LOCAL;


/**
 * @author CH
 */
public class LocalEventBus extends AbstractEventbus {

    private final Table<String, Class<?>, WatchDog> listener = ImmutableCollection.<String, Class<?>, WatchDog>newTable().build();
    private final Map<EventbusEvent, WatchObserver> cache = new ConcurrentHashMap<>();

    public LocalEventBus(Profile profile) {
        super(profile);
    }

    @Override
    public Eventbus register(EventbusEvent[] value) {
        for (EventbusEvent eventbusEvent : value) {
            String name = eventbusEvent.getName();
            Class<?> paramType = eventbusEvent.getParamType();

            synchronized (listener) {
                WatchDog watchDog = listener.get(name, paramType);
                if (null == watchDog) {
                    synchronized (listener) {
                        if (null == watchDog) {
                            watchDog = new WatchDog();
                        }
                    }
                    listener.put(name, paramType, watchDog);
                }
                cache.put(eventbusEvent, new WatchObserver(eventbusEvent));
                watchDog.addObserver(cache.get(eventbusEvent));
            }
        }
        return this;
    }

    @Override
    public Eventbus unregister(EventbusEvent eventbusEvent) {
        String name = eventbusEvent.getName();
        Class<?> paramType = eventbusEvent.getParamType();
        WatchDog watchDog = listener.get(name, paramType);

        WatchObserver watchObserver = cache.get(eventbusEvent);
        if (null != watchDog && null != watchObserver) {
            watchDog.deleteObserver(watchObserver);
        }

        return this;
    }

    @Override
    public Eventbus post(String name, Object message) {
        if (null == message) {
            return this;
        }
        Collection<WatchDog> watchDogs = null;
        if (StringUtils.isNullOrEmpty(name)) {
            watchDogs = listener.values();
        } else {
            WatchDog watchDog = listener.get(name, message.getClass());
            if (null != watchDog) {
                watchDogs = Collections.singletonList(watchDog);
            }
        }

        if (null != watchDogs) {
            watchDogs.forEach(it -> {
                executor.execute(() -> {
                    it.post(message);
                });
            });
        }

        return this;
    }

    @Override
    public EventbusType event() {
        return LOCAL;
    }

    /**
     * 观察者
     */
    static final class WatchDog extends Observable {

        /**
         * 通知
         *
         * @param message 消息
         */
        public void post(Object message) {
            setChanged();
            notifyObservers(message);
        }
    }

    /**
     * 观察对象
     */
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    static final class WatchObserver implements Observer {

        private final EventbusEvent eventbusEvent;

        @Override
        public void update(Observable o, Object arg) {
            Object bean = eventbusEvent.getBean();
            Method method = eventbusEvent.getMethod();
            Class<?> paramType = eventbusEvent.getParamType();
            if (paramType.isAssignableFrom(arg.getClass())) {
                method.setAccessible(true);
                try {
                    method.invoke(bean, new Object[]{arg});
                } catch (Exception ignored) {
                }
            }
        }
    }
}
