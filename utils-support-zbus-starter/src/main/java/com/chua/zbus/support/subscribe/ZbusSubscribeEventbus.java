package com.chua.zbus.support.subscribe;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.eventbus.*;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.json.Json;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.zbus.broker.Broker;
import org.zbus.broker.BrokerConfig;
import org.zbus.broker.HaBroker;
import org.zbus.broker.SingleBroker;
import org.zbus.mq.Consumer;
import org.zbus.mq.MqConfig;
import org.zbus.mq.Producer;
import org.zbus.mq.Protocol;
import org.zbus.net.http.Message;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author CH
 */
@Slf4j
public class ZbusSubscribeEventbus extends AbstractEventbus implements InitializingAware, Consumer.ConsumerHandler {

    public String address;
    private final String endpoint;
    private Consumer consumer;
    private Producer producer;
    Broker broker;

    private final Map<String, Set<EventbusEvent>> temp = new HashMap<>();

    /**
     * zbus订阅eventbus
     *
     * @param address  住址
     * @param endpoint 端点
     */
    public ZbusSubscribeEventbus(String address, String endpoint) {
        this.address = address;
        this.endpoint = endpoint;
        afterPropertiesSet();
    }
    public ZbusSubscribeEventbus(String endpoint) {
        this("127.0.0.1:55555", endpoint);
    }
    public  ZbusSubscribeEventbus() {
        this("127.0.0.1:55555", "mq");
    }

    @Override
    public SubscribeEventbus register(EventbusEvent[] value) {
        for (EventbusEvent eventbusEvent : value) {
            String name = eventbusEvent.getName();
            if (StringUtils.isNullOrEmpty(name)) {
                continue;
            }
            temp.computeIfAbsent(name, it -> new HashSet<>()).add(eventbusEvent);
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

        Set<EventbusEvent> subscribeTasks = temp.get(name);
        if (null != subscribeTasks) {
            List<EventbusEvent> list = intoRemoveList(subscribeTasks, value);
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(subscribeTasks::remove);
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
        Message message1 = new Message();
        message1.setBody(Json.toJsonByte(ImmutableBuilder.builderOfStringMap().put("name", name).put("data", new EventbusMessage(message)).build()));
        try {
            producer.sendAsync(message1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public EventbusType event() {
        return EventbusType.ZBUS;
    }

    @Override
    public void afterPropertiesSet() {
        //创建Broker代表
        BrokerConfig brokerConfig = new BrokerConfig();
        brokerConfig.setBrokerAddress(address);
        try {
            if (address.contains(CommonConstant.SYMBOL_COMMA)) {
                broker = new HaBroker(brokerConfig);
            } else {
                broker = new SingleBroker(brokerConfig);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MqConfig mqConfig1 = new MqConfig();
        mqConfig1.setBroker(broker);
        mqConfig1.setMq(endpoint);
        this.producer = new Producer(mqConfig1);
        try {
            producer.createMQAsync((it) -> {
                log.info(it.getBodyString());
            });
        } catch (Exception ignored) {
        }

        try {
            BrokerConfig config = new BrokerConfig();
            config.setBrokerAddress(address);
            MqConfig mqConfig = new MqConfig();
            mqConfig.setBroker(broker);
            mqConfig.setMode(Protocol.MqMode.MQ);
            mqConfig.setMq(endpoint);
            this.consumer = new Consumer(mqConfig);
            consumer.onMessage(this);
            consumer.start();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void handle(Message msg, Consumer consumer) throws IOException {
        String utf8Str = StringUtils.utf8Str(msg.getBody());
        JSONObject jsonObject = Json.fromJson(utf8Str, JSONObject.class);
        String name = jsonObject.getString("name");
        EventbusMessage eventbusMessage = jsonObject.getObject("data", EventbusMessage.class);
        Set<EventbusEvent> eventbusEvents = temp.get(name);
        if(null == eventbusMessage || null == eventbusEvents) {
            return;
        }

        invoke(eventbusEvents, eventbusMessage);
    }

    @Override
    public void close() throws Exception {
        super.close();
        consumer.stop();
        if(null != broker) {
            broker.close();
        }
    }
}
