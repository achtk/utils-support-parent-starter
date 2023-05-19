package com.chua.common.support.eventbus;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.ThreadUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * 消息总线
 *
 * @author CH
 */
public abstract class AbstractEventbus implements Eventbus {
    private final List<EventbusEvent> eventbusEvents = new LinkedList<>();
    protected Executor executor;
    protected Profile profile;

    public AbstractEventbus(Profile profile) {
        this.profile = profile;
    }

    @Override
    public Eventbus executor(Executor executor) {
        this.executor = ObjectUtils.defaultIfNull(executor, ThreadUtils.newSingleThreadExecutor("eventbus-0"));
        return this;
    }

    @Override
    public Eventbus register(EventbusEvent... eventbusEvent) {
        eventbusEvents.addAll(Arrays.asList(eventbusEvent));
        return this;
    }

    @Override
    public Eventbus unregister(EventbusEvent value) {
        eventbusEvents.remove(value);
        return this;
    }

    @Override
    public void close() throws Exception {
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdown();
        }
    }


    /**
     * 执行
     *
     * @param value    对象集合
     * @param fromJson 参数
     */
    protected void invoke(Collection<? extends EventbusEvent> value, EventbusMessage fromJson) {
        if (null == fromJson) {
            return;
        }

        Class<?> type = ClassUtils.forName(fromJson.getType());
        if (null == type) {
            return;
        }
        value.forEach(it -> {
            if (it.getParamType().isAssignableFrom(type)) {
                Object bean = it.getBean();
                Method method = it.getMethod();
                Object json = Converter.convertIfNecessary(fromJson.getMessage(), type);
                if (null != json) {
                    method.setAccessible(true);
                    try {
                        method.invoke(bean, new Object[]{json});
                    } catch (Exception ignored) {
                    }
                }
            }
        });

    }
}
