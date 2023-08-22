package com.chua.common.support.os;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PlatformKey
 *
 * @author CH
 */
public class PlatformKey {
    private final OS os;
    private final String key;

    private Map<OS, String> cache = new ConcurrentHashMap<>();

    public PlatformKey(OS os, String key) {
        this.os = os;
        this.key = key;
        this.add(os, key);
    }

    /**
     * 添加索引
     *
     * @param os  os
     * @param key key
     */
    private void add(OS os, String key) {
        cache.put(os, key);
    }

    /**
     * 添加索引
     *
     * @param key key
     */
    public PlatformKey isOsx(String key) {
        add(OS.OSX, key);
        return this;
    }

    /**
     * 添加索引
     *
     * @param key key
     */
    public PlatformKey isWindow(String key) {
        add(OS.WINDOWS, key);
        return this;
    }
    /**
     *  linux
     * @param key key
     * @return this
     */
    public static PlatformKey isAny(String key) {
        return new PlatformKey(OS.ANY, key);
    }
    /**
     * 添加索引
     *
     * @param key key
     */
    public PlatformKey isLinux(String key) {
        add(OS.LINUX, key);
        return this;
    }

    @Override
    public String toString() {
        return cache.getOrDefault(OS.getCurrent(), cache.get(OS.ANY));
    }
}
