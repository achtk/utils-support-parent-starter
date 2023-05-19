package com.chua.redis.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import org.redisson.api.RedissonClient;

/**
 * @author CH
 */
@Spi("redission")
public class RedissionProvider extends AbstractClientProvider<RedissonClient> {

    public RedissionProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return RedissionClient.class;
    }
}
