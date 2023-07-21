package com.chua.common.support.crypto.encode;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.crypto.utils.DigestUtils;

import javax.crypto.Cipher;

/**
 * aes
 *
 * @author CH
 */
@Spi("aes-cbc")
public class AesCbcEncode implements KeyEncode {

    public static final String KEY_ALGORITHM = "AES";
    /**
     * 默认的加密算法
     */
    public static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            // 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, DigestUtils.getSecretKey(KEY_ALGORITHM, key, 128));
            // 加密
            return cipher.doFinal(content);
        } catch (Exception ignored) {
        }
        return null;
    }


}
