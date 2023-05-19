package com.chua.kafka.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.kafka.support.template.KafkaTemplate;

/**
 * kafka
 * @author CH
 */
@Spi("kafka")
public class KafkaClientProvider extends AbstractClientProvider<KafkaTemplate> {

    public KafkaClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return KafkaClient.class;
    }

}
