package com.chua.common.support.extra.quickio.api;

/**
 * kv
 * @author Administrator
 */
public interface Kv extends AutoCloseable {
    /**
     * 关闭
     */
    @Override
    void close();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 写入
     * @param key key
     * @param value value
     * @param <K> 类型
     * @param <V> 类型
     */
    <K, V> void write(K key, V value);

    /**
     * 读取
     * @param key key
     * @param defaultValue 值
     * @return 值
     * @param <K> 类型
     * @param <V> 类型
     */
    <K, V> V read(K key, V defaultValue);

    /**
     * 读取
     * @param key key
     * @param clazz 类型
     * @return 值
     * @param <K> 类型
     * @param <V> 类型
     */
    <K, V> V read(K key, Class<V> clazz);

    /**
     * 删除
     * @param key key
     * @return 是否成功
     * @param <K> 类型
     */
    <K> boolean erase(K key);
    /**
     * 是否存在
     * @param key key
     * @return 是否成功
     * @param <K> 类型
     */
    <K> boolean contains(K key);
    /**
     * 重命名
     * @param oldKey key
     * @param newKey key
     * @param <K> 类型
     */
    <K> void rename(K oldKey, K newKey);
    /**
     * 类型
     * @param key key
     * @return 类型
     * @param <K> 类型
     */
    <K> String type(K key);
}
