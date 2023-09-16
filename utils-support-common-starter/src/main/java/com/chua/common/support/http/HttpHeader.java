package com.chua.common.support.http;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 消息头
 *
 * @author CH
 */

public class HttpHeader implements Iterable<Map.Entry<String, String>> {

    private final Map<String, String> headers = new LinkedHashMap<>();

    public HttpHeader() {
    }

    public HttpHeader(Iterable<Map.Entry<String, String>> headers) {
        if (null == headers) {
            return;
        }
        headers.forEach((k) -> {
            addHeader(k.getKey(), k.getValue());
        });
    }


    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().iterator();
    }


    /**
     * 每个
     *
     * @param consumer 消费者
     */
    public void forEach(BiConsumer<String, String> consumer) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 为空
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return headers.isEmpty();
    }

    /**
     * 去除消息头
     *
     * @param headerName 消息头名称
     * @return {@link HttpHeader}
     */
    public HttpHeader removeHeader(String headerName) {
        headers.remove(headerName);
        return this;
    }

    /**
     * 添加消息头
     *
     * @param headerName  消息头名称
     * @param headerValue 消息头值
     * @return {@link HttpHeader}
     */
    public HttpHeader addHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        return this;
    }

    /**
     * 添加消息头
     *
     * @param headerName  消息头名称
     * @param headerValue 消息头值
     * @return {@link HttpHeader}
     */
    public HttpHeader set(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        return this;
    }

    /**
     * 获取消息头
     *
     * @param headerName 消息头名称
     * @return {@link String}
     */
    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    /**
     * 获取消息头
     *
     * @param headerName   消息头名称
     * @param defaultValue 违约值
     * @return {@link String}
     */
    public String getHeader(String headerName, String defaultValue) {
        return headers.getOrDefault(headerName, defaultValue);
    }

    /**
     * 价值观
     *
     * @return {@link Iterable}<{@link ?} {@link extends} {@link String}>
     */
    public Iterable<? extends String> values() {
        return headers.values();
    }

    /**
     * 密钥集
     *
     * @return {@link Iterable}<{@link ?} {@link extends} {@link String}>
     */
    public Iterable<? extends String> keySet() {
        return headers.keySet();
    }

    /**
     * 作为简单地图
     *
     * @return {@link Map}<{@link String}, {@link String}>
     */
    public Map<String, String> asSimpleMap() {
        return headers;
    }
}