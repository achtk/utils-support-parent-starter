package com.chua.redis.support.disruptor;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.json.Json;
import com.chua.common.support.task.disruptor.*;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.redis.support.util.JedisUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * redis
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/30
 */
@Slf4j
@NoArgsConstructor
public class RedisDisruptor<E> extends AbstractDisruptor<E> implements Disruptor<E> {

    private static final InheritableThreadLocal<JedisPool> SHARDED_POOL = new InheritableThreadLocal<>();
    private final AtomicLong count = new AtomicLong(0);
    @Setter
    private Properties properties;

    public RedisDisruptor(Properties properties) {
        this.properties = properties;
    }

    public RedisDisruptor(String host) {
        Properties properties = new Properties();
        properties.put("host", host);
        this.properties = properties;
    }

    @Override
    public void configuration(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void publish(E message) {
        try (Jedis shardedJedis = SHARDED_POOL.get().getResource()) {
            shardedJedis.rpush(type.getName(), Json.toJson(new DisruptorMessage<>(message, count.getAndIncrement(), false)));
        }
    }

    @Override
    public void initial(Class<E> type, int bufferSize, ThreadFactory threadFactory, Executor executor, EntityFactory<E> entityFactory) {
        super.initial(type, bufferSize, threadFactory, executor, entityFactory);
        this.executor = ThreadUtils.newExecutor(threadFactory, executor, process);
        this.initialServer();
    }

    @Override
    public void start() {
        super.start();
        IntStream.range(0, process).forEach(it -> {
            this.executor.execute(new RedisDisruptorRunnable(this));
        });
    }

    /**
     * 初始化Jedis
     */
    private synchronized void initialServer() {
        if (SHARDED_POOL.get() == null) {
            SHARDED_POOL.set(JedisUtil.getShardedJedisPool(properties));
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        SHARDED_POOL.get().close();
    }

    @AllArgsConstructor
    final class RedisDisruptorRunnable implements Runnable {
        private final RedisDisruptor<E> redisDisruptor;

        @Override
        public void run() {
            while (redisDisruptor.isRunning()) {
                if (null == SHARDED_POOL.get()) {
                    redisDisruptor.initialServer();
                }
                try (Jedis redis = SHARDED_POOL.get().getResource()) {
                    String message = redis.lpop(type.getName());
                    if (null == message) {
                        continue;
                    }
                    DisruptorMessage<E> fromJson = Json.fromJson(message, DisruptorMessage.class);
                    if (null == fromJson) {
                        continue;
                    }
                    EventActor<? super E> handler = CollectionUtils.getRandom(handles);
                    E property = BeanUtils.copyProperties(fromJson.getElement(), type);
                    if (null == property) {
                        continue;
                    }
                    if (null != handler) {
                        try {
                            handler.onEvent(property, fromJson.getSeq(), fromJson.isEndOfBatch());
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
    }
}
