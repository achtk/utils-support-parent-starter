package com.chua.common.support.utils;

import com.chua.common.support.crypto.mac.HashMac;
import com.chua.common.support.crypto.mac.HmacAlgorithm;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加解密
 *
 * @author CH
 */
public class DigestUtils {

    private static final int KEY_SIZE = 1024;
    private static final String MD2 = "MD2";
    private static final String MD5 = "MD5";
    private static final String RSA = "RSA";
    public static final String SHA_1 = "SHA-1";
    /**
     * The SHA-224 hash algorithm defined in the FIPS PUB 180-3.
     * <p>
     * Present in Oracle Java 8.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA_224 = "SHA-224";

    /**
     * The SHA-256 hash algorithm defined in the FIPS PUB 180-2.
     */
    public static final String SHA_256 = "SHA-256";

    /**
     * The SHA-384 hash algorithm defined in the FIPS PUB 180-2.
     */
    public static final String SHA_384 = "SHA-384";

    /**
     * The SHA-512 hash algorithm defined in the FIPS PUB 180-2.
     */
    public static final String SHA_512 = "SHA-512";

    /**
     * The SHA-512 hash algorithm defined in the FIPS PUB 180-4.
     * <p>
     * Included starting in Oracle Java 9.
     * </p>
     *
     * @since 1.14
     */
    public static final String SHA_512_224 = "SHA-512/224";

    /**
     * The SHA-512 hash algorithm defined in the FIPS PUB 180-4.
     * <p>
     * Included starting in Oracle Java 9.
     * </p>
     *
     * @since 1.14
     */
    public static final String SHA_512_256 = "SHA-512/256";

    /**
     * The SHA3-224 hash algorithm defined in the FIPS PUB 202.
     * <p>
     * Included starting in Oracle Java 9.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA3_224 = "SHA3-224";

    /**
     * The SHA3-256 hash algorithm defined in the FIPS PUB 202.
     * <p>
     * Included starting in Oracle Java 9.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA3_256 = "SHA3-256";

    /**
     * The SHA3-384 hash algorithm defined in the FIPS PUB 202.
     * <p>
     * Included starting in Oracle Java 9.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA3_384 = "SHA3-384";

    /**
     * The SHA3-512 hash algorithm defined in the FIPS PUB 202.
     * <p>
     * Included starting in Oracle Java 9.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA3_512 = "SHA3-512";

    private static final int STREAM_BUFFER_LENGTH = 1024;

    /**
     * 使用KeyPairGenerator生成密钥对KeyPair
     * KeyPair对象中有公钥、私钥
     *
     * 其中，KeyPairGenerator.getInstance(algorithm)支持的算法有：RSA、DSA
     * 全部支持的算法见官方文档
     * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     */
    public static KeyPair newKeyPair(String algorithm) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 密钥
     *
     * @param publicKey 密钥
     * @param algorithm 加密方式
     * @return 加密结果
     * @throws Exception e'x
     */
    public static KeyFactory getKeyFactory(byte[] publicKey, String algorithm) throws Exception {
        return KeyFactory.getInstance(algorithm);
    }

    /**
     * 密钥
     *
     * @param publicKey 密钥
     * @return 加密结果
     * @throws Exception e'x
     */
    public static X509EncodedKeySpec getX509EncodedKeySpec(byte[] publicKey) throws Exception {
        return new X509EncodedKeySpec(publicKey);
    }

    /**
     * md2
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] md2(final byte[] data) {
        return getMd2Digest().digest(data);
    }

    /**
     * md2
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] md2(final InputStream data) throws IOException {
        return digest(getMd2Digest(), data);
    }


    /**
     * md2
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] md2(final String data) {
        return md2(StringUtils.utf8Bytes(data));
    }

    /**
     * md2
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String md2Hex(final byte[] data) {
        return Hex.encodeHexString(md2(data));
    }

    /**
     * md2
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String md2Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(md2(data));
    }


    /**
     * md2
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String md2Hex(final String data) {
        return Hex.encodeHexString(md2(data));
    }

    /**
     * md5
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] md5(final byte[] data) {
        return getMd5Digest().digest(data);
    }

    /**
     * md5
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] md5(final InputStream data) throws IOException {
        return digest(getMd5Digest(), data);
    }


    /**
     * md5
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] md5(final String data) {
        return md5(StringUtils.utf8Bytes(data));
    }

    /**
     * md5
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String md5Hex(final byte[] data) {
        return Hex.encodeHexString(md5(data));
    }

    /**
     * md5
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String md5Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(md5(data));
    }


    /**
     * md5
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String md5Hex(final String data) {
        return Hex.encodeHexString(md5(data));
    }

    /**
     * sha1
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha1(final byte[] data) {
        return getSha1Digest().digest(data);
    }

    /**
     * sha1
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha1(final InputStream data) throws IOException {
        return digest(getSha1Digest(), data);
    }


    /**
     * sha1
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha1(final String data) {
        return sha1(StringUtils.utf8Bytes(data));
    }

    /**
     * sha1
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha1Hex(final byte[] data) {
        return Hex.encodeHexString(sha1(data));
    }

    /**
     * sha1
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha1Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha1(data));
    }


    /**
     * sha1
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha1Hex(final String data) {
        return Hex.encodeHexString(sha1(data));
    }

    /**
     * sha256
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha256(final byte[] data) {
        return getSha256Digest().digest(data);
    }

    /**
     * sha256
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha256(final InputStream data) throws IOException {
        return digest(getSha256Digest(), data);
    }


    /**
     * sha256
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha256(final String data) {
        return sha256(StringUtils.utf8Bytes(data));
    }

    /**
     * sha256
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha256Hex(final byte[] data) {
        return Hex.encodeHexString(sha256(data));
    }

    /**
     * sha256
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha256Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha256(data));
    }


    /**
     * sha256
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha256Hex(final String data) {
        return Hex.encodeHexString(sha256(data));
    }

    /**
     * sha384
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha384(final byte[] data) {
        return getSha384Digest().digest(data);
    }

    /**
     * sha384
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha384(final InputStream data) throws IOException {
        return digest(getSha384Digest(), data);
    }


    /**
     * sha384
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha384(final String data) {
        return sha384(StringUtils.utf8Bytes(data));
    }

    /**
     * sha384
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha384Hex(final byte[] data) {
        return Hex.encodeHexString(sha384(data));
    }

    /**
     * sha384
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha384Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha384(data));
    }


    /**
     * sha384
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha384Hex(final String data) {
        return Hex.encodeHexString(sha384(data));
    }

    /**
     * sha512
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha512(final byte[] data) {
        return getSha512Digest().digest(data);
    }

    /**
     * sha512
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha512(final InputStream data) throws IOException {
        return digest(getSha512Digest(), data);
    }


    /**
     * sha512
     *
     * @param data 数据
     * @return 加密结果
     */
    public static byte[] sha512(final String data) {
        return sha512(StringUtils.utf8Bytes(data));
    }

    /**
     * sha512
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha512Hex(final byte[] data) {
        return Hex.encodeHexString(sha512(data));
    }

    /**
     * sha512
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha512Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha512(data));
    }


    /**
     * sha512
     *
     * @param data 数据
     * @return 加密结果
     */
    public static String sha512Hex(final String data) {
        return Hex.encodeHexString(sha512(data));
    }

    /**
     * 读取 InputStream 并返回数据的摘要
     *
     * @param messageDigest 要使用的 MessageDigest  (e.g. MD5)
     * @param data          要加密的数据
     * @return the digest
     * @throws IOException On error reading from the stream
     * @since 1.11 (was private)
     */
    public static byte[] digest(final MessageDigest messageDigest, final InputStream data) throws IOException {
        return updateDigest(messageDigest, data).digest();
    }

    /**
     * 读取 InputStream 并更新数据的摘要
     *
     * @param digest      要使用的 MessageDigest (e.g. MD5)
     * @param inputStream 要加密的数据
     * @return the digest
     * @throws IOException On error reading from the stream
     * @since 1.8
     */
    public static MessageDigest updateDigest(final MessageDigest digest, final InputStream inputStream)
            throws IOException {
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            digest.update(buffer, 0, read);
            read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return digest;
    }


    /**
     * 信息摘要
     *
     * @return 信息摘要
     */
    public static MessageDigest getMd2Digest() {
        return getDigest(MD2);
    }

    /**
     * 信息摘要
     *
     * @return 信息摘要
     */
    public static MessageDigest getMd5Digest() {
        return getDigest(MD5);
    }

    /**
     * 信息摘要
     *
     * @return 信息摘要
     */
    public static MessageDigest getSha1Digest() {
        return getDigest(SHA_1);
    }

    /**
     * 信息摘要
     *
     * @return 信息摘要
     */
    public static MessageDigest getSha256Digest() {
        return getDigest(SHA_256);
    }

    /**
     * 信息摘要
     *
     * @return 信息摘要
     */
    public static MessageDigest getSha384Digest() {
        return getDigest(SHA_384);
    }

    /**
     * 信息摘要
     *
     * @return 信息摘要
     */
    public static MessageDigest getSha512Digest() {
        return getDigest(SHA_512);
    }

    /**
     * 信息摘要
     *
     * @param algorithm 算法
     * @return 信息摘要
     */
    public static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 生成加密秘钥
     *
     * @param algorithm 算法
     * @param key       seed
     * @return SecretKeySpec
     */
    public static SecretKeySpec getSecretKey(final String algorithm, final String key) {
        try {
            return new SecretKeySpec(StringUtils.utf8Bytes(key), algorithm);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @param algorithm 算法
     * @param key       seed
     * @return SecretKeySpec
     */
    public static SecretKeySpec getSecretKey(final String algorithm, final byte[] key) {
        try {
            return new SecretKeySpec(key, algorithm);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @param algorithm 算法
     * @param key       seed
     * @param length    长度
     * @return SecretKeySpec
     */
    public static SecretKeySpec getSecretKey(final String algorithm, final String key, final int length) {
        return getSecretKey(algorithm, StringUtils.utf8Bytes(key), length);
    }

    /**
     * 生成加密秘钥
     *
     * @param algorithm 算法
     * @param key       seed
     * @param length    长度
     * @return SecretKeySpec
     */
    public static SecretKeySpec getSecretKey(final String algorithm, final byte[] key, final int length) {
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(algorithm);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key);
            if (length < 1) {
                kg.init(secureRandom);
            } else {
                kg.init(length, secureRandom);
            }
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), algorithm);
        } catch (NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    /**
     * rsa生成密钥对
     *
     * @return key
     * @throws Exception ex
     */
    public static PrivateKey generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair.getPrivate();
    }


    /**
     * 创建HMac对象，调用digest方法可获得hmac值
     *
     * @param algorithm {@link HmacAlgorithm}
     * @param key       密钥，如果为{@code null}生成随机密钥
     * @return {@link HashMac}
     * @since 3.0.3
     */
    public static HashMac hmac(HmacAlgorithm algorithm, byte[] key) {
        return new HashMac(algorithm, key);
    }

    /**
     * 创建HMac对象，调用digest方法可获得hmac值
     *
     * @param algorithm {@link HmacAlgorithm}
     * @param key       密钥{@link SecretKey}，如果为{@code null}生成随机密钥
     * @return {@link HashMac}
     * @since 3.0.3
     */
    public static HashMac hmac(HmacAlgorithm algorithm, SecretKey key) {
        return new HashMac(algorithm, key);
    }


    /**
     * AES解密
     *
     * @param encryptStr 密文
     * @param decryptKey 秘钥，必须为16个字符组成
     * @return 明文
     * @throws Exception Exception
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) {
        if (StringUtils.isEmpty(encryptStr) || StringUtils.isEmpty(decryptKey)) {
            return null;
        }

        byte[] encryptByte = Base64.getDecoder().decode(encryptStr);
        byte[] decryptBytes = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
            decryptBytes = cipher.doFinal(encryptByte);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return new String(decryptBytes);
    }

    /**
     * AES加密
     *
     * @param content    明文
     * @param encryptKey 秘钥，必须为16个字符组成
     * @return 密文
     * @throws Exception Exception
     */
    public static String aesEncrypt(String content, String encryptKey) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(encryptKey)) {
            return null;
        }

        byte[] encryptStr = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

            encryptStr = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(encryptStr);
    }
}
