package com.chua.common.support.crypto.decode;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.crypto.utils.DigestUtils;

import javax.crypto.Cipher;

import static com.chua.common.support.crypto.encode.AesEncode.DEFAULT_CIPHER_ALGORITHM;
import static com.chua.common.support.crypto.encode.AesEncode.KEY_ALGORITHM;

/**
 * 解密
 *
 * @author CH
 */
@Spi("aes")
public class AesDecode implements KeyDecode {
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
            System.out.println();
        }
        return null;
    }
}
