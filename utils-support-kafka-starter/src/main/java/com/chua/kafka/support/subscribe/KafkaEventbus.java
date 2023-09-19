package com.chua.kafka.support.subscribe;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.eventbus.*;
import com.chua.common.support.json.Json;
import com.chua.common.support.protocol.client.Client;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.kafka.support.KafkaClientProvider;
import com.chua.kafka.support.template.KafkaTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.chua.common.support.eventbus.EventbusType.KAFKA;

/**
 * 订阅发布
 *
 * @author CH
 */
@Spi("kafka")
public class KafkaEventbus extends AbstractEventbus implements AutoCloseable {

    private static final AtomicBoolean IS_RUNNING = new AtomicBoolean(false);
    private final Map<String, Set<EventbusEvent>> temp = new HashMap<>();
    private final List<EventbusEvent> empty = new ArrayList<>();
    KafkaTemplate kafkaTemplate;

    public KafkaEventbus(String address, String groupId) {
        this.kafkaTemplate = initialKafka(address, groupId);
        if(null != kafkaTemplate) {
            IS_RUNNING.set(true);
        }
    }
    public KafkaEventbus(String groupId) {
        this("127.0.0.1:9092", groupId);
    }
    public KafkaEventbus(String host, int port, String user, String passwd, String groupId) {
        this(host + ":" + port, groupId);
    }
    public KafkaEventbus() {
        this("127.0.0.1:9092", null);
    }

    public KafkaEventbus(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        IS_RUNNING.set(true);
    }

    @Override
    public EventbusType event() {
        return KAFKA;
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
        kafkaTemplate.subscribe(temp.keySet().toArray(new String[0]), 1000, consumerRecords1 -> {
            if (null == consumerRecords1) {
                return;
            }
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords1) {
                String topic = consumerRecord.topic();
                String value1 = consumerRecord.value();
                Set<EventbusEvent> subscribeTasks = temp.get(topic);

                EventbusMessage eventbusMessage = Json.fromJson(value1, EventbusMessage.class);
                invoke(subscribeTasks, eventbusMessage);
                invoke(empty, eventbusMessage);
            }
        }, (ExecutorService) executor);
        return this;
    }

    private KafkaTemplate initialKafka(String address, String groupId) {
        KafkaClientProvider kafkaClientProvider = new KafkaClientProvider(
                ClientOption.newDefault().executor(executor)
                        .ream(ImmutableBuilder.builderOfStringMap(Object.class)
                                .put("group.id", groupId).build()
                        )
        );
        Client<KafkaTemplate> templateClient = kafkaClientProvider.create();
        templateClient.connect(address);
        try {
            return templateClient.getClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                    subscribeTasks.removeAll(list);
                }
            }
        }
        return this;
    }

    /**
     * 获取删除的对象
     *
     * @param eventbusEvents 数据源
     * @param eventbusEvent  比较数据
     * @return 删除的对象
     */
    private List<EventbusEvent> intoRemoveList(Collection<EventbusEvent> eventbusEvents, EventbusEvent eventbusEvent) {
        List<EventbusEvent> list = new ArrayList<>();
        Method method1 = eventbusEvent.getMethod();
        eventbusEvents.forEach(it -> {
            if (it.getBean() != eventbusEvent.getBean()) {
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

        kafkaTemplate.send(name.split(","), Json.toJson(new EventbusMessage(message)));
        return this;
    }



    @Override
    public void close() throws Exception {
        IS_RUNNING.set(false);
        ThreadUtils.closeQuietly(executor);
        kafkaTemplate.close();
    }
}
