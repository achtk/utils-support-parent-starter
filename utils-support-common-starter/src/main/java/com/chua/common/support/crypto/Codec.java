package com.chua.common.support.crypto;


import com.chua.common.support.crypto.decode.KeyDecode;
import com.chua.common.support.crypto.encode.KeyEncode;

/**
 * 加解密
 *
 * @author CH
 */
public interface Codec extends KeyDecode, KeyEncode {
    /**
     * 设置密钥
     *
     * @param accessKey 密钥
     * @return this
     */
    Codec accessKey(String accessKey);

    /**
     * 设置密钥
     *
     * @param secretKey 密钥
     * @return this
     */
    Codec secretKey(String secretKey);
}
