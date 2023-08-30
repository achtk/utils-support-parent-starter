package com.chua.common.support.extra.quickio.core;

import com.chua.common.support.extra.quickio.api.Kv;
import com.chua.common.support.extra.quickio.exception.QuException;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import static com.chua.common.support.extra.quickio.core.Constants.KV_PATH;

/**
 * 配置
 * @author CH
 */
final class QuKv implements Kv {

    private final EngineIo engine;


    QuKv(Config config) {
        if (config.path == null) {
            config.path = KV_PATH;
        } else {
            config.path = Paths.get(config.path, KV_PATH).toAbsolutePath().toString();
        }
        engine = new EngineLevel().open(config);
    }


    QuKv(String name) {
        this(Config.of(c -> c.name(name)));
    }


    @Override
    public void close() {
        engine.close();
    }

    @Override
    public void destroy() {
        engine.destroy();
    }


    @Override
    public <K, V> void write(K key, V value) {
        engine.put(Codec.encode(key), Codec.encode(value));
    }


    @SuppressWarnings("unchecked")
    @Override
    public <K, V> V read(K key, V defaultValue) {
        byte[] bytes = engine.get(Codec.encode(key));
        if (bytes != null) {
            Object object = Codec.decode(bytes, defaultValue.getClass());
            return (object == null) ? defaultValue : (V) object;
        }
        return defaultValue;
    }


    @Override
    public <K, V> V read(K key, Class<V> clazz) {
        byte[] bytes = engine.get(Codec.encode(key));
        if (bytes != null) {
            Object object = Codec.decode(bytes, clazz);
            return (object != null) ? clazz.cast(object) : null;
        }
        return null;
    }


    @Override
    public <K> boolean erase(K key) {
        engine.delete(Codec.encode(key));
        return true;
    }


    @Override
    public <K> boolean contains(K key) {
        byte[] bytes = engine.get(Codec.encode(key));
        return bytes != null;
    }


    @Override
    public <K> void rename(K oldKey, K newKey) {
        byte[] oldKeyBytes = Codec.encode(oldKey);
        byte[] newKeyBytes = Codec.encode(newKey);
        if (Arrays.equals(oldKeyBytes, newKeyBytes)) {
            return;
        }
        if (engine.get(newKeyBytes) != null) {
            throw new QuException(Constants.KEY_ALREADY_EXISTS_AND_NOT_AVAILABLE);
        }
        byte[] valueBytes = engine.get(oldKeyBytes);
        Optional.ofNullable(valueBytes).ifPresent(bytes -> engine.writeBatch(batch -> {
            engine.put(newKeyBytes, valueBytes);
            engine.delete(oldKeyBytes);
        }));
    }


    @Override
    public <K> String type(K key) {
        byte[] bytes = engine.get(Codec.encode(key));
        return (bytes != null) ? Codec.getClassName(bytes) : null;
    }

}