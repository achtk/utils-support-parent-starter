package com.chua.common.support.eventbus;

import com.chua.common.support.lang.exception.NotSupportedException;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static com.chua.common.support.lang.code.ReturnResultCode.OK;
import static com.chua.common.support.lang.code.ReturnResultCode.RESOURCE_NOT_FOUND;

/**
 * 事件
 *
 * @author CH
 */
@Slf4j
final class EventbusImpl implements Eventbus{

    private final Map<String, SubscribeEventbus> eventBusPool = new ConcurrentHashMap<>();
    private final Executor executor;

    {
        eventBusPool.put(EventbusType.DEFAULT.name(), new LocalSubscribeEventBus());
        eventBusPool.put(EventbusType.GUAVA.name(), new GuavaSubscribeEventBus());
    }

    public EventbusImpl(Executor executor) {
        this.executor = executor;
        for (SubscribeEventbus eventbus : eventBusPool.values()) {
            eventbus.executor(executor);
            eventbus.build();
        }
    }

    /**
     * 注册
     *
     * @param name 名称
     * @return {@link EventbusImpl}
     */
    @Override
    public EventbusImpl registerSubscriber(String name, SubscribeEventbus eventbus) {
        if(null == eventbus) {
            throw new NullPointerException("注册器不能为空");
        }

        name = name.toUpperCase();
        SubscribeEventbus subscribeEventbus = eventBusPool.get(name);
        if(null != subscribeEventbus) {
            unregisterSubscriber(name);
        }
        eventbus.executor(executor);
        eventbus.build();
        eventBusPool.put(name, eventbus);
        return this;
    }

    /**
     * 注销
     *
     * @param name 名称
     * @return {@link EventbusImpl}
     */
    @Override
    public EventbusImpl unregisterSubscriber(String name) {
        name = name.toUpperCase();
        SubscribeEventbus subscribeEventbus = eventBusPool.get(name);
        if(null != subscribeEventbus) {
            try {
                subscribeEventbus.close();
                eventBusPool.remove(name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }


    /**
     * 注册对象
     *
     * @param eventbusEvent event
     * @return this
     */
    @Override
    public EventbusImpl register(EventbusEvent... eventbusEvent) {
        for (EventbusEvent event : eventbusEvent) {
            String name = event.getType().name();
            SubscribeEventbus subscribeEventbus = eventBusPool.get(name);
            if(null == subscribeEventbus) {
                log.error(StringUtils.format("{}不存在", name));
                return this;
            }

            subscribeEventbus.register(event);
        }
        return this;
    }

    /**
     * 注销
     *
     * @param eventbusEvent event
     * @return this
     */
    @Override
    public EventbusImpl unregister(EventbusEvent eventbusEvent) {
        String name = eventbusEvent.getType().name();
        SubscribeEventbus subscribeEventbus = eventBusPool.get(name);
        if(null == subscribeEventbus) {
            throw new NotSupportedException(StringUtils.format("{}不存在", name));
        }

        subscribeEventbus.unregister(eventbusEvent);
        return this;
    }

    /**
     * 邮递
     * 下发消息
     *
     * @param name          名称
     * @param message       消息
     * @param subscribeName 订阅名称
     * @return 结果
     */
    @Override
    public EventbusResult post(String subscribeName, String name, Object message) {
        List<SubscribeEventbus> subscribeEventbusList = new LinkedList<>();
        if(null == subscribeName) {
            subscribeEventbusList.addAll(eventBusPool.values());
        } else {
            SubscribeEventbus subscribeEventbus = eventBusPool.get(subscribeName.toUpperCase());
            if(null != subscribeEventbus) {
                subscribeEventbusList.add(subscribeEventbus);
            }
        }

        if(subscribeEventbusList.isEmpty()) {
            return EventbusResult.builder().code(RESOURCE_NOT_FOUND).msg("订阅器不存在").build();
        }

        for (SubscribeEventbus subscribeEventbus : subscribeEventbusList) {
            subscribeEventbus.post(name, message);
        }

        return EventbusResult.builder().code(OK).build();
    }

    @Override
    public void shutdown() {
        for (SubscribeEventbus subscribeEventbus : eventBusPool.values()) {
            try {
                subscribeEventbus.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
