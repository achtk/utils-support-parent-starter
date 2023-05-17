package com.chua.common.support.http;


import com.chua.common.support.collection.MultiLinkedValueMap;
import com.chua.common.support.collection.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求体
 *
 * @author CH
 */

public class HttpBody extends MultiLinkedValueMap<String, Object> implements MultiValueMap<String, Object> {

    public HttpBody() {
    }


    public HttpBody(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public void putList(Map<String, List<Object>> map) {
        for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
            this.putAll(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 简单化集合
     *
     * @return 简单化集合
     */
    public Map<String, Object> asSimpleMap() {
        Map<String, Object> body = new HashMap<>(1 << 4);
        this.forEach(body::put);
        return body;
    }


    /**
     * 添加数据
     *
     * @param bodyName  名称
     * @param bodyValue 值
     * @return 结果
     */
    public HttpBody addBody(String bodyName, Object bodyValue) {
        this.put(bodyName, bodyValue);
        return this;
    }

    /**
     * 添加数据
     *
     * @param bodyName  名称
     * @param bodyValue 值
     * @return 结果
     */
    public HttpBody addBody(String bodyName, Object... bodyValue) {
        for (Object s : bodyValue) {
            addBody(bodyName, s);
        }
        return this;
    }
}