package com.chua.common.support.json.jsonpath.spi.json;

import com.chua.common.support.json.Json;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.json.jsonpath.InvalidJsonException;
import com.chua.common.support.utils.StringUtils;

import java.io.InputStream;

/**
 * fastjson
 *
 * @author CH
 */
public class FastjsonJsonProvider extends AbstractJsonProvider {
    @Override
    public Object parse(String json) throws InvalidJsonException {
        if (StringUtils.trim(json).startsWith("[")) {
            return Json.getJsonArray(json);
        }
        return Json.getJsonObject(json);
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        return Json.getJsonObject(jsonStream, charset);
    }

    @Override
    public String toJson(Object obj) {
        return Json.toJson(obj);
    }

    @Override
    public Object createArray() {
        return new JsonArray();
    }

    @Override
    public Object createMap() {
        return new JsonObject();
    }
}
