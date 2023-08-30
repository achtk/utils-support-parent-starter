package com.chua.common.support.extra.quickio.struct;

import com.chua.common.support.extra.quickio.core.IoEntity;

import java.util.HashMap;

/**
 * 对象
 * @author CH
 */
public final class Document extends IoEntity {

    private final HashMap<Object, Object> docs;


    public Document() {
        docs = new HashMap<>();
    }


    public Document put(Object key, Object value) {
        docs.put(key, value);
        return this;
    }


    public <K> Object get(K key) {
        return docs.get(key);
    }

}