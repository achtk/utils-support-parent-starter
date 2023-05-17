package com.chua.common.support.utils;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 加解密
 *
 * @author CH
 */
public class CodecUtils {


    /**
     * base64
     *
     * @param bytes 数据
     * @return base64 加密
     */
    public static String encodeBase64(byte[] bytes) {
        try {
            return new String(Base64.getEncoder().encode(bytes), UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param base64 数据
     * @return 数据
     */
    public static byte[] decodeBase64(String base64) {
        try {
            return Base64.getDecoder().decode(base64.getBytes(UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
