package com.chua.common.support.utils;


import com.chua.common.support.json.Json;

/**
 * 拷贝
 * @author Administrator
 */
public class CloneUtils {

    /**
     * 拷贝
     *
     * @param t     对象
     * @param clazz 类型
     * @param <T>   类型
     * @return 结果
     */
    public static <T> T clone(T t, Class<T> clazz) {
        if (null == t) {
            return null;
        }
        return Json.fromJson(Json.toJson(t), clazz);
    }
}
