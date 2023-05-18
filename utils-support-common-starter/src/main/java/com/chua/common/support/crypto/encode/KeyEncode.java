package com.chua.common.support.crypto.encode;

import com.chua.common.support.crypto.Hex;
import com.chua.common.support.utils.StringUtils;

/**
 * 加密
 *
 * @author CH
 */
public interface KeyEncode extends Encode {

    /**
     * 加密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    byte[] encode(byte[] content, byte[] key);

    /**
     * 加密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    default byte[] encode(String content, byte[] key) {
        return encode(StringUtils.utf8Bytes(content), key);
    }

    /**
     * 加密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    default byte[] encode(byte[] content, String key) {
        return encode(content, StringUtils.utf8Bytes(key));
    }

    /**
     * 加密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    default byte[] encode(String content, String key) {
        return encode(StringUtils.utf8Bytes(content), StringUtils.utf8Bytes(key));
    }

    /**
     * 加密
     *
     * @param content 内容
     * @return 加密结果
     */
    @Override
    default byte[] encode(byte[] content) {
        return encode(content, "");
    }

    /**
     * 加密
     *
     * @param content 内容
     * @param key     密钥
     * @return 加密结果
     */
    default String encodeHex(String content, String key) {
        return Hex.encodeHexString(this.encode(content, key));
    }
}
