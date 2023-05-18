package com.chua.common.support.crypto.utils;


import com.chua.common.support.crypto.bouncycastle.GlobalBouncyCastleProvider;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/**
 * 安全相关工具类<br>
 * 加密分为三种：<br>
 * 1、对称加密（symmetric），例如：AES、DES等<br>
 * 2、非对称加密（asymmetric），例如：RSA、DSA等<br>
 * 3、摘要加密（digest），例如：MD5、SHA-1、SHA-256、HMAC等<br>
 *
 * @author Looly, Gsealy
 */
public class SecureUtils {
    /**
     * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成
     *
     * @param algorithm 算法，支持PBE算法
     * @return {@link SecretKey}
     */
    public static SecretKey generateKey(String algorithm) {
        return KeyUtils.generateKey(algorithm);
    }

    /**
     * 默认密钥字节数
     *
     * <pre>
     * RSA/DSA
     * Default Keysize 1024
     * Keysize must be a multiple of 64, ranging from 512 to 1024 (inclusive).
     * </pre>
     */
    public static final int DEFAULT_KEY_SIZE = KeyUtils.DEFAULT_KEY_SIZE;

    /**
     * 创建{@link Mac}
     *
     * @param algorithm 算法
     * @return {@link Mac}
     * @since 4.5.13
     */
    public static Mac createMac(String algorithm) {
        final Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

        Mac mac;
        try {
            mac = (null == provider) ? Mac.getInstance(algorithm) : Mac.getInstance(algorithm, provider);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

        return mac;
    }
}
