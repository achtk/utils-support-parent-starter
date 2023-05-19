package com.chua.redis.support.util;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.NetAddress;
import com.chua.common.support.utils.StringUtils;
import com.chua.redis.support.config.RedisConfiguration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.EMPTY_STRING_ARRAY;

/**
 * jedis工具类
 *
 * @author CH
 * @version 1.0.0
 */
public class JedisUtil {

    /**
     * 获取连接池
     *
     * @param properties 参数
     * @return 连接池
     */
    public static JedisCluster getJedisCluster(Properties properties) {
        // JedisPoolConfig
        GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig<>();
        doAnalaysisPoolConfig(config, properties);

        String[] hosts = MapUtils.getStringArray(properties, "host");
        hosts = Arrays.stream(hosts).filter(StringUtils::isNotEmpty).toArray(String[]::new);
        if (hosts.length == 0) {
            hosts = new String[]{"localhost:6379"};
        }
        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        HostAndPort hostAndPort = null;
        for (String s : hosts) {
            NetAddress netAddress = NetAddress.of(s);
            hostAndPortSet.add(new HostAndPort(netAddress.getHost(), netAddress.getPort()));
        }

        return new JedisCluster(hostAndPortSet, config);
    }

    /**
     * 获取连接池
     *
     * @param configuration 参数
     * @return 连接池
     */
    public static JedisPool getShardedJedisPool(RedisConfiguration configuration) {
        // JedisPoolConfig
        GenericObjectPoolConfig<Jedis> config = new GenericObjectPoolConfig<>();
        doAnalaysisPoolConfig(config, MapUtils.asProp(BeanMap.create(configuration)));

        String hosts = configuration.getAddress();
        if (null != hosts) {
            NetAddress netAddress = NetAddress.of(hosts);
            return new JedisPool(config, netAddress.getHost(), netAddress.getPort());
        }

        if (null != configuration.getHost()) {
            return new JedisPool(config, configuration.getHost(), configuration.getPort());
        }

        return new JedisPool(config);
    }

    /**
     * 获取连接池
     *
     * @param properties 参数
     * @return 连接池
     */
    public static JedisPool getShardedJedisPool(Properties properties) {
        // JedisPoolConfig
        GenericObjectPoolConfig<Jedis> config = new GenericObjectPoolConfig<>();
        doAnalaysisPoolConfig(config, properties);

        String hosts = MapUtils.getString(properties, "host");
        NetAddress netAddress = NetAddress.of(hosts);
        return new JedisPool(config, netAddress.getHost(), netAddress.getPort());
    }

    /**
     * 配置参数
     *
     * @param config     配置
     * @param properties 参数
     */
    private static void doAnalaysisPoolConfig(GenericObjectPoolConfig<?> config, Properties properties) {
        config.setMaxTotal(MapUtils.getIntValue(properties, "maxTotal", 200));
        config.setMaxIdle(MapUtils.getIntValue(properties, "maxIdle", 150));
        config.setMinIdle(MapUtils.getIntValue(properties, "minIdle", 100));
        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWait(Duration.of(10, ChronoUnit.SECONDS));
        // 在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(MapUtils.getBoolean(properties, "testOnBorrow", false));
        // 调用returnObject方法时，是否进行有效检查
        config.setTestOnReturn(MapUtils.getBoolean(properties, "testOnReturn", false));
        // Idle时进行连接扫描
        config.setTestWhileIdle(MapUtils.getBoolean(properties, "testWhileIdle", true));
        // 表示idle object evitor两次扫描之间要sleep的毫秒数
        config.setTimeBetweenEvictionRuns(Duration.of(30, ChronoUnit.SECONDS));
        // 表示idle object evitor每次扫描的最多的对象数
        config.setNumTestsPerEvictionRun(10);
        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        config.setMinEvictableIdleTime(Duration.of(60, ChronoUnit.SECONDS));
    }

    /**
     * 获取连接池
     *
     * @return 连接池
     */
    public static Jedis createDefaultJedis() {
        return getJedis(RedisConfiguration.defaultConfiguration());
    }

    /**
     * 获取连接池
     *
     * @param redisConfiguration 参数
     * @return 连接池
     */
    public static Jedis getJedis(RedisConfiguration redisConfiguration) {
        return getJedis(BeanMap.create(redisConfiguration));
    }

    /**
     * 获取连接池
     *
     * @param properties 参数
     * @return 连接池
     */
    public static Jedis getJedis(Properties properties) {
        return getJedis(properties);
    }

    /**
     * 获取连接池
     *
     * @param properties 参数
     * @return 连接池
     */
    public static Jedis getJedis(Map<String, Object> properties) {
        // JedisPoolConfig
        String host = MapUtils.getString(properties, "host", "127.0.0.1");
        String user = MapUtils.getString(properties, "user");
        String password = MapUtils.getString(properties, "password");
        Integer database = MapUtils.getIntValue(properties, "database", 0);
        Integer connectionTimeoutMs = MapUtils.getIntValue(properties, "connectionTimeoutMs");
        Integer port = MapUtils.getIntValue(properties, "port", 6379);

        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();
        if (!StringUtils.isNullOrEmpty(password)) {
            builder.password(password);
        }

        if (!StringUtils.isNullOrEmpty(user)) {
            builder.user(user);
        }

        if (null != connectionTimeoutMs) {
            builder.connectionTimeoutMillis(connectionTimeoutMs);
        }

        builder.database(database);

        return new Jedis(new HostAndPort(host, port), builder.build());
    }

    /**
     * 获取连接池
     *
     * @param redisConfiguration 参数
     * @return 连接池
     */
    public static JedisPool getJedisPool(RedisConfiguration redisConfiguration) {
        // JedisPoolConfig
        String host = redisConfiguration.getHost();
        String user = redisConfiguration.getUser();
        String password = redisConfiguration.getPassword();
        Integer database = redisConfiguration.getDatabase();
        Integer connectionTimeoutMs = redisConfiguration.getConnectionTimeoutMs();
        Integer port = redisConfiguration.getPort();

        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();
        if (!StringUtils.isNullOrEmpty(password)) {
            builder.password(password);
        }

        if (!StringUtils.isNullOrEmpty(user)) {
            builder.user(user);
        }

        if (null != connectionTimeoutMs) {
            builder.connectionTimeoutMillis(connectionTimeoutMs);
        }

        builder.database(database);

        return new JedisPool(new HostAndPort(host, port), builder.build());
    }

    /**
     * 获取连接池
     *
     * @param properties 参数
     * @return 连接池
     */
    public static JedisPool getJedisPool(Properties properties) {
        // JedisPoolConfig
        String host = MapUtils.getString(properties, "host", "127.0.0.1");
        String user = MapUtils.getString(properties, "user");
        String password = MapUtils.getString(properties, "password");
        Integer database = MapUtils.getIntValue(properties, "database", 0);
        Integer connectionTimeoutMs = MapUtils.getIntValue(properties, "connectionTimeoutMs");
        Integer port = MapUtils.getIntValue(properties, "port", 6379);

        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();
        if (!StringUtils.isNullOrEmpty(password)) {
            builder.password(password);
        }

        if (!StringUtils.isNullOrEmpty(user)) {
            builder.user(user);
        }

        if (null != connectionTimeoutMs) {
            builder.connectionTimeoutMillis(connectionTimeoutMs);
        }

        builder.database(database);

        return new JedisPool(new HostAndPort(host, port), builder.build());
    }

    /**
     * 获取值
     *
     * @param key       索引
     * @param jedisPool jedis
     * @return 获取的值
     */
    public static String getValue(String key, final JedisPool jedisPool) {
        return execute(jedis -> jedis.get(key), jedisPool);
    }

    /**
     * 获取值
     *
     * @param key       索引
     * @param jedisPool jedis
     * @return 获取的值
     */
    public static byte[] getValue(byte[] key, final JedisPool jedisPool) {
        return execute(jedis -> jedis.get(key), jedisPool);
    }

    /**
     * 删除
     *
     * @param key       索引
     * @param jedisPool jedis
     * @return 获取的值
     */
    public static Long del(String key, final JedisPool jedisPool) {
        return execute(jedis -> jedis.del(key), jedisPool);
    }

    /**
     * 设置
     *
     * @param key       索引
     * @param value     值
     * @param jedisPool jedis
     * @return {value}
     */
    public static String set(final String key, final String value, final JedisPool jedisPool) {
        return execute(jedis -> jedis.set(key, value), jedisPool);
    }

    /**
     * 索引是否存在
     *
     * @param key       索引
     * @param jedisPool jedis
     * @return {value}
     */
    public static Boolean exists(final String key, final JedisPool jedisPool) {
        return execute(jedis -> jedis.exists(key), jedisPool);
    }

    /**
     * 索引是否存在
     *
     * @param key       索引
     * @param jedisPool jedis
     * @return {value}
     */
    public static Long exists(final Set<String> key, final JedisPool jedisPool) {
        return execute(jedis -> jedis.exists(key.toArray(EMPTY_STRING_ARRAY)), jedisPool);
    }

    /**
     * 获取消息队列数据长度
     *
     * @param queueName 索引
     * @param jedisPool jedis
     * @return {value}
     */
    public static Long lLen(final String queueName, final JedisPool jedisPool) {
        return execute(jedis -> jedis.llen(queueName), jedisPool);
    }

    /**
     * 设置消息队列数据(左侧)
     *
     * @param queueName 索引
     * @param data      数据
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static Long lPushx(final String queueName, final String data, final JedisPool jedisPool) {
        return execute(jedis -> jedis.lpushx(queueName, data), jedisPool);
    }

    /**
     * 设置消息队列数据(右侧)
     *
     * @param queueName 索引
     * @param data      数据
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static Long rPushx(final String queueName, final String data, final JedisPool jedisPool) {
        return execute(jedis -> jedis.rpush(queueName, data), jedisPool);
    }

    /**
     * 获取消息队列数据(左侧)
     *
     * @param queueName 索引
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static String lPop(final String queueName, final JedisPool jedisPool) {
        return execute(jedis -> jedis.lpop(queueName), jedisPool);
    }

    /**
     * 获取消息队列数据(左侧)
     *
     * @param queueName 索引
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static List<String> blPop(final String queueName, final JedisPool jedisPool) {
        return execute(jedis -> jedis.blpop(-1, queueName), jedisPool);
    }

    /**
     * 获取消息队列数据(左侧)
     *
     * @param queueNames 索引
     * @param jedisPool  jedis
     * @return 数据长度
     */
    public static List<String> blPop(final Set<String> queueNames, final JedisPool jedisPool) {
        return execute(jedis -> jedis.blpop(-1, queueNames.toArray(EMPTY_STRING_ARRAY)), jedisPool);
    }

    /**
     * 获取消息队列数据(右侧)<rpop：非阻塞式>
     *
     * @param queueName 索引
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static String rPop(final String queueName, final JedisPool jedisPool) {
        return execute(jedis -> jedis.rpop(queueName), jedisPool);
    }

    /**
     * 获取消息队列数据(右侧)<brpop：阻塞式>
     *
     * @param queueName 索引
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static List<String> brPop(final String queueName, final JedisPool jedisPool) {
        return execute(jedis -> jedis.brpop(-1, queueName), jedisPool);
    }

    /**
     * 获取消息队列数据(右侧)<brpop：阻塞式>
     *
     * @param queueNames 索引
     * @param jedisPool  jedis
     * @return 数据长度
     */
    public static List<String> brPop(final Set<String> queueNames, final JedisPool jedisPool) {
        return execute(jedis -> jedis.brpop(-1, queueNames.toArray(EMPTY_STRING_ARRAY)), jedisPool);
    }

    /**
     * 索引设置超时时间
     *
     * @param key       索引
     * @param second    索引超时时间
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static Long timeout(final String key, final int second, final JedisPool jedisPool) {
        return execute(jedis -> jedis.expire(key, second), jedisPool);
    }

    /**
     * 获取所有匹配的索引
     *
     * @param key       索引
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static Set<String> keys(final String key, final JedisPool jedisPool) {
        return execute(jedis -> jedis.keys(key), jedisPool);
    }

    /**
     * 查看值
     *
     * @param key       索引
     * @param start     开始
     * @param end       结束 0 到 -1代表所有值
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static List<String> lRange(final String key, final long start, final long end, final JedisPool jedisPool) {
        return execute(jedis -> jedis.lrange(key, start, end), jedisPool);
    }

    /**
     * 发布
     *
     * @param channel   管道
     * @param message   数据
     * @param jedisPool jedis
     * @return 数据长度
     */
    public static Long publish(final String channel, final String message, final JedisPool jedisPool) {
        return execute(jedis -> jedis.publish(channel, message), jedisPool);
    }


    /**
     * 执行
     *
     * @param function  回调
     * @param jedisPool jedis
     * @param <T>       类型
     * @return 数据
     */
    public static <T> T execute(final Function<Jedis, T> function, final JedisPool jedisPool) {
        if (null == jedisPool || jedisPool.isClosed()) {
            throw new IllegalStateException("redis未连接");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return function.apply(jedis);
        }
    }

    /**
     * 执行
     *
     * @param consumer  回调
     * @param jedisPool jedis
     * @return 数据
     */
    public static void consumer(final Consumer<Jedis> consumer, final JedisPool jedisPool) {
        if (null == jedisPool || jedisPool.isClosed()) {
            throw new IllegalStateException("redis未连接");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            consumer.accept(jedis);
        }
    }
}
