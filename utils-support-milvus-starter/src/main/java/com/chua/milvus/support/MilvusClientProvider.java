package com.chua.milvus.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import io.milvus.client.MilvusServiceClient;

/**
 * @author CH
 */
@Spi("Milvus")
public class MilvusClientProvider extends AbstractClientProvider<MilvusServiceClient> {

    public MilvusClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return MilvusClient.class;
    }
}
