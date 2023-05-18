package com.chua.common.support.crypto.encode;

import com.chua.common.support.crypto.utils.DigestUtils;
import com.chua.common.support.annotations.Spi;

import javax.crypto.Cipher;

/**
 * idea
 *
 * @author CH
 */
@Spi("idea")
public class IdeaEncode implements KeyEncode {

    public static final String KEY_ALGORITHM = "IDEA";

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        try {
            //加密
            Cipher cipher = Cipher.getInstance("IDEA/ECB/ISO10126Padding");
            cipher.init(Cipher.ENCRYPT_MODE, DigestUtils.getSecretKey(KEY_ALGORITHM, key, 128));
            return cipher.doFinal(content);
        } catch (Exception ignored) {
        }
        return null;
    }

}
