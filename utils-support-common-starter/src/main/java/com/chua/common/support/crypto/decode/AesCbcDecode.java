package com.chua.common.support.crypto.decode;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.crypto.utils.DigestUtils;

import javax.crypto.Cipher;

import static com.chua.common.support.crypto.encode.AesCbcEncode.DEFAULT_CIPHER_ALGORITHM;
import static com.chua.common.support.crypto.encode.AesCbcEncode.KEY_ALGORITHM;

/**
 * 解密
 *
 * @author CH
 */
@Spi("aes-cbc")
public class AesCbcDecode implements KeyDecode {
    @Override
    public byte[] decode(byte[] content, byte[] key) {
        try {
            // 实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            // 使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, DigestUtils.getSecretKey(KEY_ALGORITHM, key, 128));
            //执行操作
            return cipher.doFinal(content);
        } catch (Exception ignored) {
        }
        return null;
    }
}
