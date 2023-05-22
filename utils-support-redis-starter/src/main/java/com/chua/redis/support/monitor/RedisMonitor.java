package com.chua.redis.support.monitor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.json.Json;
import com.chua.common.support.monitor.AbstractMonitor;
import com.chua.common.support.monitor.NotifyMessage;
import com.chua.common.support.monitor.NotifyType;
import com.chua.common.support.utils.NumberUtils;
import com.chua.redis.support.config.RedisConfiguration;
import com.chua.redis.support.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CH
 */
@Spi("redis")
public class RedisMonitor extends AbstractMonitor {
    private Jedis jedis;
    private RedisSessionListener redisSessionListener;

    @Override
    public void preStart() {
        RedisConfiguration redisConfiguration = new RedisConfiguration();
        redisConfiguration.setUsername(configuration.username());
        redisConfiguration.setPort(netAddress.getPort(6379));
        redisConfiguration.setHost(netAddress.getHost("127.0.0.1"));
        redisConfiguration.setPassword(configuration.password());
        redisConfiguration.setDatabase(NumberUtils.toInt(configuration.database(), 0));
        redisConfiguration.setConnectTimeout(Duration.of(configuration.timeout(), ChronoUnit.MILLIS));

        this.jedis = JedisUtil.getJedis(redisConfiguration);
        jedis.set("notify", "新浪微博：小叶子一点也不逗");
        jedis.expire("notify", 10);
        this.redisSessionListener = new RedisSessionListener();
    }

    @Override
    public void afterStart() {
        jedis.subscribe(redisSessionListener, "__keyevent@0__:expired");

    }

    @Override
    public void preStop() {

    }

    @Override
    public void afterStop() {
        jedis.disconnect();
    }

    final class RedisSessionListener extends JedisPubSub {

        /**
         * 取得按表达式的方式订阅的消息后的处理
         */
        @Override
        public void onMessage(String channel, String message) {
            Map<String, String> item = new HashMap<>(3);
            item.put("channel", channel);
            item.put("message", message);
            notifyMessage(NotifyMessage.builder().message(Json.toJson(item)).type(NotifyType.DELETE).build());
        }
    }
}
