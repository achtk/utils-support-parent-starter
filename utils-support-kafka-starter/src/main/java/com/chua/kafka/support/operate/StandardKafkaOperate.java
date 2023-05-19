package com.chua.kafka.support.operate;

import com.chua.common.support.http.HttpHeader;
import com.chua.kafka.support.template.KafkaTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * 操作
 * @author CH
 */
public class StandardKafkaOperate implements KafkaOperate {
    private final KafkaTemplate kafkaTemplate;

    public StandardKafkaOperate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void subscribe(String[] topics, long timeoutMs, Consumer<ConsumerRecords<String, String>> consumer, ExecutorService executorService) {
        kafkaTemplate.subscribe(topics, timeoutMs, consumer, executorService);
    }

    @Override
    public void send(String[] topics, Integer partition, String key, String value, HttpHeader headers) {
        kafkaTemplate.send(topics, partition, key, value, headers);
    }
}
