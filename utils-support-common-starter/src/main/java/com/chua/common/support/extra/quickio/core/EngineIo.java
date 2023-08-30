package com.chua.common.support.extra.quickio.core;

import org.iq80.leveldb.WriteBatch;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
/**
 * engine
 * @author CH
 */
interface EngineIo extends AutoCloseable {
    /**
     * 打开
     * @param config 配置
     * @return this
     */
    EngineIo open(Config config);

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
     * 写
     * @param key key
     * @param value value
     */
    void put(byte[] key, byte[] value);

    /**
     * 删除
     * @param key key
     */
    void delete(byte[] key);

    /**
     * 读
     * @param key key
     * @return value
     */
    byte[] get(byte[] key);

    /**
     * 写
     * @param consumer 回调
     */
    void writeBatch(Consumer<WriteBatch> consumer);

    /**
     * 遍历
     * @param consumer 回调
     */
    void iteration(BiConsumer<byte[], byte[]> consumer);
    /**
     * 遍历
     * @param function 回调
     * @return T
     */
    <T> T iteration(BiFunction<byte[], byte[], T> function);
}