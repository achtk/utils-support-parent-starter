package com.chua.common.support.utils;

import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Md5
 *
 * @author ali
 */
public class Md5Utils {
    private static final int DIGITS_SIZE = 16;
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final Map<Character, Integer> CHARACTER_INTEGER_MAP = new ConcurrentHashMap<>();

    static {
        for (int i = 0; i < DIGITS.length; ++i) {
            CHARACTER_INTEGER_MAP.put(DIGITS[i], i);
        }
    }

    private static final Md5Utils ME = new Md5Utils();
    private final MessageDigest digest;
    private final ReentrantLock lock = new ReentrantLock();

    private Md5Utils() {
        try {
            digest = MessageDigest.getInstance("Md5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static Md5Utils getInstance() {
        return ME;
    }

    public String getMd5String(String content) {
        return bytes2string(hash(content));
    }

    public String getMd5String(byte[] content) {
        return bytes2string(hash(content));
    }

    public byte[] getMd5Bytes(byte[] content) {
        return hash(content);
    }

    /**
     * 对字符串进行md5
     *
     * @param str
     * @return Md5 byte[16]
     */
    public byte[] hash(String str) {
        lock.lock();
        try {
            byte[] bt = digest.digest(str.getBytes(UTF_8));
            if (null == bt || bt.length != DIGITS_SIZE) {
                throw new IllegalArgumentException("Md5 need");
            }
            return bt;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 对二进制数据进行md5
     *
     * @param data
     * @return Md5 byte[16]
     */
    public byte[] hash(byte[] data) {
        lock.lock();
        try {
            byte[] bt = digest.digest(data);
            if (null == bt || bt.length != DIGITS_SIZE) {
                throw new IllegalArgumentException("Md5 need");
            }
            return bt;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 将一个字节数组转化为可见的字符串
     *
     * @param bt
     * @return
     */
    public String bytes2string(byte[] bt) {
        int l = bt.length;

        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & bt[i]) >>> 4];
            out[j++] = DIGITS[0x0F & bt[i]];
        }

        return new String(out);
    }

}
