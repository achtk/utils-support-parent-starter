package com.chua.common.support.json.jsonpath.spi.cache;

import com.chua.common.support.json.jsonpath.InvalidJsonException;
import com.chua.common.support.json.jsonpath.JsonPath;

/**
 * @author Administrator
 */
public interface Cache {

    /**
     * Get the Cached JsonPath
     *
     * @param key cache key to lookup the JsonPath
     * @return JsonPath
     */
    JsonPath get(String key);

    /**
     * Add JsonPath to the cache
     *
     * @param key   cache key to store the JsonPath
     * @param value JsonPath to be cached
     * @throws InvalidJsonException
     */
    void put(String key, JsonPath value);
}
