package com.chua.common.support.http;

import com.chua.common.support.collection.MultiLinkedValueMap;
import com.chua.common.support.collection.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息头
 *
 * @author CH
 */

public class HttpHeader extends MultiLinkedValueMap<String, String> implements MultiValueMap<String, String> {

    public HttpHeader() {
    }


    public HttpHeader(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public void putList(Map<String, List<String>> map) {
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            this.putAll(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 简单化集合
     *
     * @return 简单化集合
     */
    public Map<String, String> asSimpleMap() {
        Map<String, String> header = new HashMap<>(1 << 4);
        this.forEach(header::put);
        return header;
    }


    /**
     * 添加数据
     *
     * @param headerName  名称
     * @param headerValue 值
     * @return 结果
     */
    public HttpHeader addHeader(String headerName, String headerValue) {
        this.put(headerName, headerValue);
        return this;
    }

    /**
     * 添加数据
     *
     * @param headerName  名称
     * @param headerValue 值
     * @return 结果
     */
    public HttpHeader addHeader(String headerName, String... headerValue) {
        for (String s : headerValue) {
            addHeader(headerName, s);
        }
        return this;
    }
}