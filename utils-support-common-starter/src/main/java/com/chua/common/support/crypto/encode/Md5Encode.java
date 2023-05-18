package com.chua.common.support.crypto.encode;

import com.chua.common.support.annotations.Spi;

import java.security.MessageDigest;

/**
 * md5
 *
 * @author CH
 */
@Spi("md5")
public class Md5Encode implements Encode {

    public static final String KEY_ALGORITHM = "MD5";

    @Override
    public byte[] encode(byte[] content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(KEY_ALGORITHM);
            messageDigest.update(content);
            return messageDigest.digest();
        } catch (Exception ignored) {
        }
        return null;
    }

}
