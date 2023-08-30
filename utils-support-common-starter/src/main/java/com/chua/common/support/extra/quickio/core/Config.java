package com.chua.common.support.extra.quickio.core;

import java.util.function.Consumer;

/**
 * 配置
 * @author CH
 */
public final class Config {

    String name;
    String path;
    Long cacheSize;


    private Config() { }


    public Config name(String name) {
        this.name = name;
        return this;
    }


    public Config path(String path) {
        this.path = path;
        return this;
    }


    public Config cache(Long size) {
        this.cacheSize = size;
        return this;
    }


    public static Config of(Consumer<Config> consumer) {
        Config config = new Config();
        consumer.accept(config);
        return config;
    }

}