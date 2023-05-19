package com.chua.kafka.support.template;

import com.chua.common.support.http.HttpHeader;
import com.chua.kafka.support.operate.KafkaOperate;
import com.google.common.base.Strings;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * kafka template
 * @author CH
 */
public class KafkaTemplate implements AutoCloseable, KafkaOperate {
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    public KafkaTemplate(KafkaProducer<String, String> producer, KafkaConsumer<String, String> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    @Override
    public void close() throws Exception {
        if(null != producer) {
            producer.close();
            producer = null;
        }

        if(null != consumer) {
            consumer.close();
            consumer = null;
        }
    }

    @Override
    public void subscribe(String[] topics,
                          long timeoutMs,
                          Consumer<ConsumerRecords<String, String>> consumer,
                          ExecutorService executorService) {
        if(null == this.consumer) {
            return;
        }

        this.consumer.subscribe(Arrays.asList(topics));
        executorService.execute(() -> {
            while (this.consumer != null) {
                ConsumerRecords<String, String> consumerRecords = this.consumer.poll(Duration.ofMillis(timeoutMs));
                consumer.accept(consumerRecords);
            }
        });

    }

    @Override
    public void send(String[] topics, Integer partition, String key, String value, HttpHeader headers) {
        if(null == producer) {
            return;
        }

        List<Header> headers1 = new LinkedList<>();
        if(null != headers) {
            headers.forEach((k, v) -> {
                if(Strings.isNullOrEmpty(v)) {
                    return;
                }
                headers1.add(new RecordHeader(k, v.getBytes(StandardCharsets.UTF_8)));
            });
        }

        for (String topic : topics) {
            producer.send(new ProducerRecord<>(topic, partition, key, value, headers1));
        }
    }
}
