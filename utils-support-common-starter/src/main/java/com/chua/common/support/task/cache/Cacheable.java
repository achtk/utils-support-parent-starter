package com.chua.common.support.task.cache;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.value.Value;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 缓存
 *
 * @author CH
 */
public interface Cacheable extends InitializingAware, DisposableAware {
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
     * 配置
     *
     * @param key      配置
     * @param supplier 回调
     * @return this
     */
    default Value<Object> apply(String key, Supplier<Object> supplier) {
        if (exist(key)) {
            return get(key);
        }

        Value<Object> value = Value.of(supplier.get());
        put(key, value);
        return value;
    }

    /**
     * 配置
     *
     * @param config 配置
     * @return this
     */
    Cacheable configuration(Map<String, Object> config);

    /**
     * 配置
     *
     * @param config 配置
     * @return this
     */
    default Cacheable configuration(CacheConfiguration config) {
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
    boolean exist(String key);

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    Value<Object> get(String key);

    /**
     * 获取值/不存在则赋值
     *
     * @param key   索引
     * @param value 值
     * @return 值
     */
    default Value<Object> getOrPut(String key, Object value) {
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
    default Value<Object> getOrPut(String key, Supplier<?> value) {
        return getOrPut(key, value.get());
    }
    /**
     * 赋值
     *
     * @param key   索引
     * @param value 值
     * @return 值
     */
    Value<Object> put(String key, Object value);

    /**
     * 删除
     *
     * @param key 索引
     * @return 值
     */
    Value<Object> remove(String key);
}
