package com.chua.common.support.lang.spide.pipeline;

import com.chua.common.support.lang.spide.request.Request;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 结果集
 * @author CH
 */
@Data
public class ResultItems {

    private Map<String, Object> fields = new LinkedHashMap<String, Object>();

    private Request request;

    private boolean skip;

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object o = fields.get(key);
        if (o == null) {
            return null;
        }
        return (T) fields.get(key);
    }

    public Map<String, Object> getAll() {
        return fields;
    }

    public <T> ResultItems put(String key, T value) {
        fields.put(key, value);
        return this;
    }
}
