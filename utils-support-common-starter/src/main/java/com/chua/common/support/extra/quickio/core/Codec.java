package com.chua.common.support.extra.quickio.core;

import com.chua.common.support.json.Json;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.NumberConstant.NUM_2;

/**
 * 加解密
 * @author CH
 */
final class Codec {

    private final static ConcurrentHashMap<Class<?>, byte[]> CLASS_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();


    static byte[] encodeKey(long v) {
        return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(v).array();
    }


    static long decodeKey(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }


    static <T> byte[] encode(T t) {
        return Json.toJsonByte(t);
    }


    static <T> T decode(byte[] bytes, Class<T> clazz) {
        return Json.fromJson(bytes, clazz);
    }


    static <T> T clone(T t, Class<T> clazz) {
        return Json.fromJson(Json.toJson(t), clazz);
    }


    static String getClassName(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            if (b == 0) {
                break;
            } else {
                stringBuilder.append((char) (int) b);
            }
        }
        return stringBuilder.toString();
    }


    private static byte[] getClassNameBytes(Class<?> clazz) {
        byte[] bytes = CLASS_CONCURRENT_HASH_MAP.getOrDefault(clazz, null);
        if (bytes == null) {
            bytes = clazz.getSimpleName().getBytes(StandardCharsets.UTF_8);
            CLASS_CONCURRENT_HASH_MAP.put(clazz, bytes);
        }
        return bytes;
    }


    private static boolean arraysEquals(byte[] a1, byte[] a2) {
        if (a1 == a2) {
            return true;
        }
        if (a1 == null || a2 == null) {
            return false;
        }
        int length = a1.length;
        if (a2.length != length) {
            return false;
        }
        for (int i = 0, size = length / NUM_2; i <= size; i++) {
            if (a1[i] != a2[i] || a1[length - 1 - i] != a2[length - 1 - i]) {
                return false;
            }
        }
        return true;
    }

}