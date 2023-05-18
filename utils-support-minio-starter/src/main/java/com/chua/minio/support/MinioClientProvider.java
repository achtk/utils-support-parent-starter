package com.chua.minio.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import io.minio.MinioClient;

/**
 * minio
 *
 * @author CH
 */
@Spi("minio")
public class MinioClientProvider extends AbstractClientProvider<MinioClient> {

    private ClientOption option;

    public MinioClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return MinioClient.class;
    }
}
