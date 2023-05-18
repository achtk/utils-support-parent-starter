package com.chua.common.support.crypto.decode;

import com.chua.common.support.crypto.Hex;
import com.chua.common.support.annotations.Spi;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * 解密
 *
 * @author CH
 */
@Spi("rsa")
public class RsaDecode implements KeyDecode {

    private static final String ALGORITHM = "RSA";

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKeyStringToKey(key));
            return cipher.doFinal(content);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 解密
     *
     * @param publicKey 密钥
     * @param text      数据
     * @return 加密结果
     * @throws Exception e'x
     */
    public static String decodeHex(Key publicKey, String text) throws Exception {
        return Hex.encodeHexString(decode(publicKey, text));
    }
    /**
     * 解密
     *
     * @param publicKey 密钥
     * @param text      数据
     * @return 加密结果
     * @throws Exception e'x
     */
    public static byte[] decode(Key publicKey, String text) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(text.getBytes());
    }

    /**
     * 私钥字符串还原为私钥
     *
     * @param privateKeyString 私钥字符串
     * @return 私钥
     * @throws Exception ex
     */
    public PrivateKey privateKeyStringToKey(String privateKeyString) throws Exception {
        byte[] privateBytes = Base64.getDecoder().decode(privateKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
    }

    /**
     * 私钥字符串还原为私钥
     *
     * @param privateBytes 私钥
     * @return 私钥
     * @throws Exception ex
     */
    public PrivateKey privateKeyStringToKey(byte[] privateBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
    }
}
