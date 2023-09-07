package com.chua.common.support.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * 预处理
 *
 * @author CH
 */
public class Pretreatment {

    private Map<String, String> bodyExpress = new HashMap<>();
    private Map<String, String> headerExpress = new HashMap<>();

    /**
     * 添加
     *
     * @param key   钥匙
     * @param value 价值
     */
    public void addHeader(String key, String value) {
        headerExpress.put(key, value);
    }

    /**
     * 添加
     *
     * @param key   钥匙
     * @param value 价值
     */
    public void addBody(String key, String value) {
        bodyExpress.put(key, value);
    }

    /**
     * 有密钥
     *
     * @param key 钥匙
     * @return boolean
     */
    public boolean hasKey(String key) {
        return bodyExpress.containsKey(key);
    }

    /**
     * 获取身体
     *
     * @return {@link Map}<{@link String}, {@link String}>
     */
    public Map<String, String> getBody() {
        return bodyExpress;
    }


    /**
     * 获取标头
     *
     * @return {@link Map}<{@link String}, {@link String}>
     */
    public Map<String, String> getHeader() {
        return headerExpress;
    }


}
