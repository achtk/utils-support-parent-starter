package com.chua.common.support.crypto.encode;

import com.chua.common.support.crypto.utils.DigestUtils;
import com.chua.common.support.annotations.Spi;

import javax.crypto.Cipher;

/**
 * des
 *
 * @author CH
 */
@Spi("des")
public class DesEncode implements KeyEncode {

    public static final String KEY_ALGORITHM = "DES";

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        try {
            //加密
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, DigestUtils.getSecretKey(KEY_ALGORITHM, key, 0));
            return cipher.doFinal(content);
        } catch (Exception ignored) {
        }
        return null;
    }

}
