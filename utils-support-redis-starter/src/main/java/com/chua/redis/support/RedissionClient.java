package com.chua.redis.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author CH
 */
public class RedissionClient extends AbstractClient<RedissonClient> {
    private RedissonClient redisClient;

    protected RedissionClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public void afterPropertiesSet() {
        Config config = new Config();
        String[] split = url.split(",");
        if (split.length == 1) {
            config.useSingleServer()
                    .setAddress(url)
                    .setDatabase(profile.getIntValue("database", 0))
                    .setPassword(profile.getString("password", null))
                    .setTimeout((int) timeout);

            this.redisClient = Redisson.create(config);
            return;
        }

        config.useClusterServers().addNodeAddress(split)
                .setPassword(profile.getString("password", null))
                .setTimeout((int) timeout);
        this.redisClient = Redisson.create(config);
    }

    @Override
    public void connectClient() {

    }

    @Override
    public RedissonClient getClient() {
        return redisClient;
    }

    @Override
    public void close() {

    }

    @Override
    public void closeClient(RedissonClient client) {
        client.shutdown();
    }

}
