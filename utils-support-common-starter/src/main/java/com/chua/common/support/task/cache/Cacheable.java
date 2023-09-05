package com.chua.common.support.task.cache;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.value.Value;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 缓存
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public interface Cacheable<K, V> extends InitializingAware, DisposableAware {
    /**
     * 实例化缓存对象
     *
     * @param entity 实体
     * @param <T>    类型
     * @return 对象
     */
    static <T> T newCacheable(T entity) {
        return entity;
    }

    /**
     * 缓存器
     * @return 缓存器
     */
    static <K, V>Cacheable<K, V> auto() {
        return ServiceProvider.of(Cacheable.class).getExtension("guava", "juc");
    }

    /**
     * 缓存器
     * @param configuration 配置
     * @return 缓存器
     */
    static <K, V>Cacheable<K, V> auto(CacheConfiguration configuration) {
        return ServiceProvider.of(Cacheable.class).getNewExtension(new String[]{"guava", "juc"}, configuration);
    }

    /**
     * 汽车
     * 缓存器
     *
     * @param timeout 超时
     * @return 缓存器
     */
    static <K, V>Cacheable<K, V> auto(int timeout) {
        return ServiceProvider.of(Cacheable.class).getNewExtension(new String[]{"guava", "juc"}, CacheConfiguration.builder()
                        .expireAfterWrite(timeout)
                .build()
        );
    }

    /**
     * 配置
     *
     * @param key      配置
     * @param supplier 回调
     * @return this
     */
    default Value<V> apply(K key, Supplier<V> supplier) {
        if (exist(key)) {
            return get(key);
        }
        V v = supplier.get();
        return put(key, v);
    }

    /**
     * 配置
     *
     * @param config 配置
     * @return this
     */
    Cacheable<K, V> configuration(Map<String, Object> config);

    /**
     * 配置
     *
     * @param config 配置
     * @return this
     */
    default Cacheable<K, V> configuration(CacheConfiguration config) {
        BeanMap beanMap = BeanMap.create(config);
        return configuration(beanMap);
    }

    /**
     * 清空
     */
    void clear();

    /**
     * 是否存在
     *
     * @param key 索引
     * @return 值
     */
    boolean exist(K key);

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    Value<V> get(K key);

    /**
     * 获取值/不存在则赋值
     *
     * @param key   索引
     * @param value 值
     * @return 值
     */
    default Value<V> getOrPutValue(K key, V value) {
        if (exist(key)) {
            return get(key);
        }
        return put(key, value);
    }

    /**
     * 获取值/不存在则赋值
     *
     * @param key   索引
     * @param value 值
     * @return 值
     */
    @SuppressWarnings("ALL")
    default V getOrPut(K key, V value) {
        if (exist(key)) {
            return get(key).getValue();
        }
        return put(key, value).getValue();
    }

    /**
     * 获取值/不存在则赋值
     *
     * @param key   索引
     * @param value 值
     * @return 值
     */
    default Value<V> getOrPut(K key, Supplier<V> value) {
        return getOrPutValue(key, value.get());
    }
    /**
     * 赋值
     *
     * @param key   索引
     * @param value 值
     * @return 值
     */
    Value<V> put(K key, V value);

    /**
     * 删除
     *
     * @param key 索引
     * @return 值
     */
    Value<V> remove(K key);
}
