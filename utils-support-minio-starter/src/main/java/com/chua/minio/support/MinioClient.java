package com.chua.minio.support;

import com.chua.common.support.net.NetAddress;
import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.StringUtils;

/**
 * minio client
 *
 * @author CH
 */
public class MinioClient extends AbstractClient<io.minio.MinioClient> {
    private io.minio.MinioClient minioClient;
    private String endpoint;
    private NetAddress netAddress;

    protected MinioClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public io.minio.MinioClient getClient() {
        return createClient(netAddress);
    }

    @Override
    public void closeClient(io.minio.MinioClient client) {
    }

    @Override
    public void connectClient() {
        synchronized (this) {
            this.netAddress = NetAddress.of(url);
            this.endpoint = StringUtils.defaultString(netAddress.getPath(), netAddress.getAddress());
            this.minioClient = createClient(netAddress);
        }
    }

    private io.minio.MinioClient createClient(NetAddress netAddress) {
        return minioClient = io.minio.MinioClient.builder()
                .endpoint(endpoint)
                .credentials(clientOption.username(), clientOption.password())
                .build();
    }

    @Override
    public void close() {

    }

    @Override
    public void afterPropertiesSet() {

    }
}
