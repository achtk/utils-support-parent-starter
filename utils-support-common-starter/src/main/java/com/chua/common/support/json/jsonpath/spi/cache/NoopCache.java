package com.chua.common.support.json.jsonpath.spi.cache;

import com.chua.common.support.json.jsonpath.JsonPath;

/**
 * @author Administrator
 */
public class NoopCache implements Cache {

    @Override
    public JsonPath get(String key) {
        return null;
    }

    @Override
    public void put(String key, JsonPath value) {
    }
}
