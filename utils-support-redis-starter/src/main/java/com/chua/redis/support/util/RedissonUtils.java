package com.chua.redis.support.util;

import com.chua.redis.support.config.RedisConfiguration;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static com.chua.common.support.constant.CommonConstant.CUT;

/**
 * Redisson
 *
 * @author CH
 */
public class RedissonUtils {
    /**
     * redisson
     *
     * @param redisConfiguration 配置
     * @param executor           executor
     * @return 客户端
     */
    public static RedissonClient create(RedisConfiguration redisConfiguration, Executor executor) {
        try {
            String address = redisConfiguration.getAddress();
            Config config = new Config();
            config.setCodec(new JsonJacksonCodec());

            if (executor instanceof ExecutorService) {
                config.setExecutor((ExecutorService) executor);
            }

            if (address.split(CUT).length == 1) {
                config.useSingleServer()
                        .setAddress(address)
                        .setConnectTimeout(redisConfiguration.getConnectionTimeoutMs())
                        .setPassword(redisConfiguration.getPassword())
                        .setDatabase(redisConfiguration.getDatabase())
                        .setKeepAlive(true)
                        .setTimeout(redisConfiguration.getConnectionTimeoutMs());

                return Redisson.create(config);
            }

            config.useClusterServers()
                    .addNodeAddress(address.split(CUT))
                    .setConnectTimeout(redisConfiguration.getConnectionTimeoutMs())
                    .setPassword(redisConfiguration.getPassword())
                    .setKeepAlive(true)
                    .setTimeout(redisConfiguration.getConnectionTimeoutMs());
            return Redisson.create(config);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
