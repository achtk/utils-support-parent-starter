package com.chua.kafka.support.operate;

import com.chua.common.support.http.HttpHeader;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * 操作
 *
 * @author CH
 */
public interface KafkaOperate {
    /**
     * 订阅
     *
     * @param topics          topics
     * @param consumer        消费者
     */
    default void subscribe(String[] topics, Consumer<ConsumerRecords<String, String>> consumer) {
        subscribe(topics, 100L, consumer);
    }
    /**
     * 订阅
     *
     * @param topics          topics
     * @param timeoutMs       周期
     * @param consumer        消费者
     */
    default void subscribe(String[] topics, final long timeoutMs, Consumer<ConsumerRecords<String, String>> consumer) {
        subscribe(topics, timeoutMs, consumer);
    }

    /**
     * 订阅
     *
     * @param topics          topics
     * @param timeoutMs       周期
     * @param consumer        消费者
     * @param executorService 线程池
     */
    void subscribe(String[] topics, final long timeoutMs, Consumer<ConsumerRecords<String, String>> consumer, ExecutorService executorService);

    /**
     * 发送消息
     *
     * @param topics topics
     * @param value  值
     */
    default void send(String[] topics, String value) {
        send(topics, 0, null, value, null);
    }

    /**
     * 发送消息
     *
     * @param topics topics
     * @param key    key
     * @param value  值
     */
    default void send(String[] topics, String key, String value) {
        send(topics, 0, key, value, null);
    }

    /**
     * 发送消息
     *
     * @param topics    topics
     * @param partition part
     * @param key       key
     * @param value     值
     */
    default void send(String[] topics, Integer partition, String key, String value) {
        send(topics, partition, key, value, null);
    }

    /**
     * 发送消息
     *
     * @param topics    topics
     * @param partition part
     * @param key       key
     * @param value     值
     * @param headers   消息头
     */
    void send(String[] topics, Integer partition, String key, String value, HttpHeader headers);
}
