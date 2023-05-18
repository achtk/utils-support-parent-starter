package com.chua.common.support.crypto.encode;

import com.chua.common.support.crypto.Hex;
import com.chua.common.support.annotations.Spi;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * rsa
 *
 * @author CH
 */
@Spi("rsa")
public class RsaEncode implements KeyEncode {

    private static final String ALGORITHM = "RSA";

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKeyStringToKey(key));
            return cipher.doFinal(content);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 加密
     *
     * @param publicKey 密钥
     * @param text      数据
     * @return 加密结果
     * @throws Exception e'x
     */
    public static String encodeHex(Key publicKey, String text) throws Exception {
        return Hex.encodeHexString(encode(publicKey, text));
    }
    /**
     * 加密
     *
     * @param publicKey 密钥
     * @param text      数据
     * @return 加密结果
     * @throws Exception e'x
     */
    public static byte[] encode(Key publicKey, String text) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(text.getBytes());
    }

    /**
     * 公钥字符串还原为公钥
     *
     * @param publicKeyString 公钥字符串
     * @return 公钥
     * @throws Exception ex
     */
    public Key publicKeyStringToKey(String publicKeyString) throws Exception {
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
    }

    /**
     * 公钥字符串还原为公钥
     *
     * @param publicKeyString 公钥字符串
     * @return 公钥
     * @throws Exception ex
     */
    public Key publicKeyStringToKey(byte[] publicKeyString) throws Exception {
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
    }


}
