package com.chua.common.support.crypto.decode;

import com.chua.common.support.crypto.Hex;
import com.chua.common.support.utils.StringUtils;

/**
 * 解密
 *
 * @author CH
 */
public interface KeyDecode extends Decode {
    /**
     * 解密
     *
     * @param content 内容
     * @return 加密结果
     */
    default byte[] decode(String content) {
        return decode(content, "");
    }

    /**
     * 解密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    byte[] decode(byte[] content, byte[] key);

    /**
     * 解密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    default byte[] decode(String content, byte[] key) {
        return decode(StringUtils.utf8Bytes(content), key);
    }

    /**
     * 解密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    default byte[] decode(byte[] content, String key) {
        return decode(content, StringUtils.utf8Bytes(key));
    }

    @Override
    default byte[] decode(byte[] content) {
        return decode(content, "");
    }

    /**
     * 解密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    default byte[] decode(String content, String key) {
        return decode(StringUtils.utf8Bytes(content), StringUtils.utf8Bytes(key));
    }

    /**
     * 解密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    default String decodeHex(String content, String key) {
        try {
            return new String(decode(Hex.decodeHex(content), key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
