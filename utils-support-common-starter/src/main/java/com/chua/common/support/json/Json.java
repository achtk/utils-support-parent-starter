package com.chua.common.support.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * json
 *
 * @author CH
 */
public class Json {

    /**
     * 获取对象
     *
     * @param json json
     * @return JsonObject
     */
    public static JsonObject getJsonObject(String json) {
        return new JsonObject(JSON.parseObject(json));
    }

    /**
     * 获取对象
     *
     * @param json json
     * @return JsonObject
     */
    public static JsonArray getJsonArray(String json) {
        return new JsonArray(JSON.parseArray(json));
    }

    /**
     * 获取对象
     *
     * @param bytes json
     * @return JsonObject
     */
    public static JsonObject getJsonObject(byte[] bytes) {
        return new JsonObject(JSON.parseObject(new String(bytes, UTF_8)));
    }

    /**
     * 获取对象
     *
     * @param inputStreamReader json
     * @return JsonObject
     */
    public static JsonObject getJsonObject(InputStreamReader inputStreamReader) {
        try {
            return new JsonObject(JSON.parseObject(IoUtils.toString(inputStreamReader)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象
     *
     * @param inputStream json
     * @return JsonObject
     */
    public static JsonObject getJsonObject(InputStream inputStream) {
        return getJsonObject(new InputStreamReader(inputStream, UTF_8));
    }

    /**
     * 获取对象
     *
     * @param inputStream json
     * @param charset     字符编码
     * @return JsonObject
     */
    public static JsonObject getJsonObject(InputStream inputStream, String charset) {
        try {
            return getJsonObject(new InputStreamReader(inputStream, charset));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象
     *
     * @param json json
     * @return JsonObject
     */
    public static Map<String, Object> toMapStringObject(String json) {
        return fromJson(json, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 获取对象
     *
     * @param inputStream json
     * @return JsonObject
     */
    public static <T> T fromJson(InputStream inputStream, TypeReference<T> tTypeReference) {
        try {
            return JSON.parseObject(IoUtils.toString(inputStream, UTF_8), tTypeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象
     *
     * @param json json
     * @return JsonObject
     */
    public static <T> T fromJson(String json, TypeReference<T> tTypeReference) {
        return JSON.parseObject(json, tTypeReference);
    }

    /**
     * 获取对象
     *
     * @param json json
     * @return JsonObject
     */
    @SuppressWarnings("ALL")
    public static <T> T fromJson(String json, Class<T> target) {
        json = json.trim();
        if (json.startsWith("[")) {
            return (T) JSON.parseArray(json, target);
        }
        return JSON.parseObject(json, target);
    }

    /**
     * 获取对象
     *
     * @param bytes json
     * @return JsonObject
     */
    public static <T> T fromJson(byte[] bytes, Class<T> target) {
        return JSON.parseObject(bytes, target);
    }

    /**
     * 获取对象
     *
     * @param inputStreamReader json
     * @return JsonObject
     */
    public static <T> T fromJson(InputStreamReader inputStreamReader, Class<T> target) {
        try {
            return JSON.parseObject(IoUtils.toString(inputStreamReader), target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象
     *
     * @param inputStreamReader json
     * @return JsonObject
     */
    public static <T> T fromJson(InputStreamReader inputStreamReader, TypeReference<T> typeReference) {
        try {
            return fromJson(IoUtils.toString(inputStreamReader), typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象
     *
     * @param inputStream json
     * @return JsonObject
     */
    public static <T> T fromJson(InputStream inputStream, Class<T> target) {
        return JSON.parseObject(inputStream, target);
    }

    /**
     * 转为json
     *
     * @param object 对象
     * @return Json
     */
    public static String toJson(Object object) {
        return JSON.toJSONString(object, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 转为json
     *
     * @param object 对象
     * @return Json
     */
    public static String prettyFormat(Object object) {
        return JSON.toJSONString(object, "yyyy-MM-dd HH:mm:ss", JSONWriter.Feature.PrettyFormat);
    }

    /**
     * 对象转json
     *
     * @param obj 对象
     * @return json
     */
    public static String toPrettyFormat(Object obj) {
        return prettyFormat(obj);
    }

    /**
     * 转为json
     *
     * @param object 对象
     * @return Json
     */
    public static byte[] toJsonByte(Object object) {
        return JSON.toJSONBytes(object);
    }

    /**
     * 是否是json
     * @param ext 数据
     * @return 结果
     */
    public static boolean isJson(Object ext) {
        if(null == ext) {
            return false;
        }

        if(ext instanceof String) {
            ext = ext.toString().trim();
            return (((String) ext).startsWith("[") && ((String) ext).endsWith("]")) ||
             (((String) ext).startsWith("{") && ((String) ext).endsWith("}"));
        }

        return false;
    }
}
