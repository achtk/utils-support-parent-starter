package com.chua.kafka.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.kafka.support.template.KafkaTemplate;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

/**
 * kafka
 * @author CH
 */
public class KafkaClient extends AbstractClient<KafkaTemplate> {

    private KafkaTemplate kafkaTemplate;

    protected KafkaClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public void connectClient() {
        this.kafkaTemplate = createTemplate();
    }

    private KafkaTemplate createTemplate() {
        //1.创建Kafka生产者的配置信息
        Properties properties = new Properties();
        //指定链接的kafka集群
        properties.put(BOOTSTRAP_SERVERS_CONFIG, url);
        //ack应答级别
        properties.put(ACKS_CONFIG, describe().getString("ack", "all"));
        //重试次数
        properties.put(RETRIES_CONFIG, clientOption.retry());
        //批次大小
        properties.put(BATCH_SIZE_CONFIG, describe().getIntValue(BATCH_SIZE_CONFIG, 16384));
        //等待时间
        properties.put(LINGER_MS_CONFIG, describe().getIntValue(LINGER_MS_CONFIG, 1));
        //RecordAccumulator缓冲区大小
        properties.put(BUFFER_MEMORY_CONFIG, describe().getIntValue(BUFFER_MEMORY_CONFIG, 33554432));
        //Key,Value的序列化类
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, describe().getString(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer"));
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, describe().getString(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer"));
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, describe().getString(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer"));
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, describe().getString(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer"));

        Properties consumerConfig = new Properties();
        consumerConfig.putAll(properties);
        consumerConfig.put(ENABLE_AUTO_COMMIT_CONFIG, describe().getBooleanValue(ENABLE_AUTO_COMMIT_CONFIG, true));
        consumerConfig.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, describe().getIntValue(AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000));
        consumerConfig.put(GROUP_ID_CONFIG, describe().getString(GROUP_ID_CONFIG, "consumer-group-id"));
        //重置消费者offset的方法（达到重复消费的目的），设置该属性也只在两种情况下生效：1.上面设置的消费组还未消费(可以更改组名来消费)2.该offset已经过期
        consumerConfig.put(AUTO_OFFSET_RESET_CONFIG, describe().getString(AUTO_OFFSET_RESET_CONFIG,"earliest"));
        //创建生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        //创建生产者
        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(consumerConfig);

        return new KafkaTemplate(producer, consumer);
    }

    @Override
    public KafkaTemplate getClient() {
        return kafkaTemplate;
    }

    @SneakyThrows
    @Override
    public void closeClient(KafkaTemplate client) {
        client.close();
    }

    @SneakyThrows
    @Override
    public void close() {
        kafkaTemplate.close();
    }

    @Override
    public void afterPropertiesSet() {

    }
}
