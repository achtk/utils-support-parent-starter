package com.chua.redis.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * redis客户端
 *
 * @author Administrator
 */
public class RedisClient extends AbstractClient<Jedis> {
    private final GenericObjectPoolConfig<Jedis> config = new GenericObjectPoolConfig<>();

    private JedisPool jedisPool;
    RedisClient(ClientOption clientOption) {super(clientOption);}
    @Override
    public void connectClient() {
        this.jedisPool = new JedisPool(config, profile.getString("host"), profile.getIntValue("port", 6379));
    }

    @Override
    public Jedis getClient() {
        Jedis resource = jedisPool.getResource();
        if (!StringUtils.isNullOrEmpty(clientOption.database())) {
            resource.select(NumberUtils.toInt(clientOption.database()));
        }
        return resource;
    }

    @Override
    public void closeClient(Jedis client) {
        client.close();
    }


    @Override
    public void close() {
        if (null != jedisPool) {
            jedisPool.close();
        }
    }

    @Override
    public void afterPropertiesSet() {
        // JedisPoolConfig
        config.setMaxTotal(clientOption.maxTotal());
        config.setMaxIdle(clientOption.maxIdle());
        config.setMinIdle(clientOption.minIdle());
        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWait(Duration.of(clientOption.maxWaitMillis(), ChronoUnit.MILLIS));
        // 在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(MapUtils.getBoolean(clientOption.ream(), "testOnBorrow", false));
        // 调用returnObject方法时，是否进行有效检查
        config.setTestOnReturn(MapUtils.getBoolean(clientOption.ream(), "testOnReturn", false));
        // Idle时进行连接扫描
        config.setTestWhileIdle(MapUtils.getBoolean(clientOption.ream(), "testWhileIdle", true));
        // 表示idle object evitor两次扫描之间要sleep的毫秒数
        config.setTimeBetweenEvictionRuns(Duration.of(30, ChronoUnit.SECONDS));
        // 表示idle object evitor每次扫描的最多的对象数
        config.setNumTestsPerEvictionRun(10);
        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        config.setMinEvictableIdleTime(Duration.of(60, ChronoUnit.SECONDS));
    }
}
