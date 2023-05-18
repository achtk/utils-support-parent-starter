package com.chua.common.support.crypto.decode;

import com.chua.common.support.crypto.utils.DigestUtils;
import com.chua.common.support.annotations.Spi;

import javax.crypto.Cipher;

import static com.chua.common.support.crypto.encode.AesEncode.KEY_ALGORITHM;

/**
 * 解密
 *
 * @author CH
 */
@Spi("des")
public class DesDecode implements KeyDecode {

    private static final String ALGORITHM = "DES";

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        try {
            //转换密钥
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, DigestUtils.getSecretKey(KEY_ALGORITHM, key, 0));
            return cipher.doFinal(content);
        } catch (Exception ignored) {
        }
        return null;
    }
}
