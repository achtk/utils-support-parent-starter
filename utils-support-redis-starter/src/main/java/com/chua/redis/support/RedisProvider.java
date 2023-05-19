package com.chua.redis.support;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import redis.clients.jedis.Jedis;

/**
 * redis客户端
 *
 * @author Administrator
 */
@Spi("redis")
public class RedisProvider extends AbstractClientProvider<Jedis> {

    public RedisProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return RedisClient.class;
    }

}
