package com.chua.mongodb.support;

import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.mongodb.support.template.MongodbVfsTemplate;

/**
 * Mongodb
 * @author CH
 */
public class MongodbClientProvider extends AbstractClientProvider<MongodbVfsTemplate> {

    public MongodbClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return MongodbClient.class;
    }
}
