package com.chua.proxy.support.global;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局配置
 *
 * @author CH
 * @since 2023/09/16
 */
public class GlobalConfig {


    public static final GlobalConfig INSTANCE = new GlobalConfig();

    /**
     * 获取例子
     *
     * @return {@link GlobalConfig}
     */
    public GlobalConfig getInstance() {
        return INSTANCE;
    }

    private static final Map<String, Object> ATTRIBUTE = new ConcurrentHashMap<>();

    /**
     * 获取属性
     *
     * @param name 名称
     * @return {@link T}
     */
    @SuppressWarnings("ALL")
    public <T> void addAttribute(String name, T value) {
        ATTRIBUTE.put(name, value);
    }

    /**
     * 获取属性
     *
     * @param name 名称
     * @return {@link T}
     */
    @SuppressWarnings("ALL")
    public <T> T getAttribute(String name) {
        return (T) ATTRIBUTE.get(name);
    }
}