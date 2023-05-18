package com.chua.common.support.crypto.mac;

import com.chua.common.support.crypto.Hex;
import com.chua.common.support.crypto.encode.Base64Encode;
import com.chua.common.support.utils.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * hashmac
 *
 * @author CH
 */
public class HashMac {
    private final MacEngine engine;

    // ------------------------------------------------------------------------------------------- Constructor start

    /**
     * 构造，自动生成密钥
     *
     * @param algorithm 算法 {@link HmacAlgorithm}
     */
    public HashMac(HmacAlgorithm algorithm) {
        this(algorithm, (Key) null);
    }

    /**
     * 构造
     *
     * @param algorithm 算法 {@link HmacAlgorithm}
     * @param key       密钥
     */
    public HashMac(HmacAlgorithm algorithm, byte[] key) {
        this(algorithm.getValue(), key);
    }

    /**
     * 构造
     *
     * @param algorithm 算法 {@link HmacAlgorithm}
     * @param key       密钥
     */
    public HashMac(HmacAlgorithm algorithm, Key key) {
        this(algorithm.getValue(), key);
    }

    /**
     * 构造
     *
     * @param algorithm 算法
     * @param key       密钥
     * @since 4.5.13
     */
    public HashMac(String algorithm, byte[] key) {
        this(algorithm, new SecretKeySpec(key, algorithm));
    }

    /**
     * 构造
     *
     * @param algorithm 算法
     * @param key       密钥
     * @since 4.5.13
     */
    public HashMac(String algorithm, Key key) {
        this(algorithm, key, null);
    }

    /**
     * 构造
     *
     * @param algorithm 算法
     * @param key       密钥
     * @param spec      {@link AlgorithmParameterSpec}
     * @since 5.6.12
     */
    public HashMac(String algorithm, Key key, AlgorithmParameterSpec spec) {
        this(MacEngineFactory.createEngine(algorithm, key, spec));
    }

    /**
     * 构造
     *
     * @param engine MAC算法实现引擎
     * @since 4.5.13
     */
    public HashMac(MacEngine engine) {
        this.engine = engine;
    }
    // ------------------------------------------------------------------------------------------- Constructor end
// ------------------------------------------------------------------------------------------- Digest

    /**
     * 生成文件摘要
     *
     * @param data    被摘要数据
     * @param charset 编码
     * @return 摘要
     */
    public byte[] digest(String data, Charset charset) {
        return digest(StringUtils.bytes(data, charset));
    }

    /**
     * 生成文件摘要
     *
     * @param data 被摘要数据
     * @return 摘要
     */
    public byte[] digest(String data) {
        return digest(data, UTF_8);
    }

    /**
     * 生成文件摘要，并转为Base64
     *
     * @param data      被摘要数据
     * @param isUrlSafe 是否使用URL安全字符
     * @return 摘要
     */
    public String digestBase64(String data, boolean isUrlSafe) {
        return digestBase64(data, UTF_8, isUrlSafe);
    }

    /**
     * 生成文件摘要，并转为Base64
     *
     * @param data      被摘要数据
     * @param charset   编码
     * @param isUrlSafe 是否使用URL安全字符
     * @return 摘要
     */
    public String digestBase64(String data, Charset charset, boolean isUrlSafe) {
        return Base64Encode.encodeStr(digest(data, charset), false, isUrlSafe);
    }

    /**
     * 生成文件摘要，并转为16进制字符串
     *
     * @param data    被摘要数据
     * @param charset 编码
     * @return 摘要
     */
    public String digestHex(String data, Charset charset) {
        return Hex.encodeHexString(digest(data, charset));
    }

    /**
     * 生成文件摘要
     *
     * @param data 被摘要数据
     * @return 摘要
     */
    public String digestHex(String data) {
        return digestHex(data, UTF_8);
    }

    /**
     * 生成文件摘要<br>
     * 使用默认缓存大小，见 {@link  com.chua.common.support.constant.CommonConstant#DEFAULT_BUFFER_SIZE}
     *
     * @param file 被摘要文件
     * @return 摘要bytes
     * @throws IOException Cause by IOException
     */
    public byte[] digest(File file) {
        try (InputStream in = Files.newInputStream(file.toPath())) {
            return digest(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成文件摘要，并转为16进制字符串<br>
     * 使用默认缓存大小，见 {@link  com.chua.common.support.constant.CommonConstant#DEFAULT_BUFFER_SIZE}
     *
     * @param file 被摘要文件
     * @return 摘要
     */
    public String digestHex(File file) {
        return Hex.encodeHexString(digest(file));
    }

    /**
     * 生成摘要
     *
     * @param data 数据bytes
     * @return 摘要bytes
     */
    public byte[] digest(byte[] data) {
        return digest(new ByteArrayInputStream(data), -1);
    }

    /**
     * 生成摘要，并转为16进制字符串<br>
     *
     * @param data 被摘要数据
     * @return 摘要
     */
    public String digestHex(byte[] data) {
        return Hex.encodeHexString(digest(data));
    }

    /**
     * 生成摘要，使用默认缓存大小，见 {@link  com.chua.common.support.constant.CommonConstant#DEFAULT_BUFFER_SIZE}
     *
     * @param data {@link InputStream} 数据流
     * @return 摘要bytes
     */
    public byte[] digest(InputStream data) {
        return digest(data, com.chua.common.support.constant.CommonConstant.DEFAULT_BUFFER_SIZE);
    }

    /**
     * 生成摘要，并转为16进制字符串<br>
     * 使用默认缓存大小，见 {@link  com.chua.common.support.constant.CommonConstant#DEFAULT_BUFFER_SIZE}
     *
     * @param data 被摘要数据
     * @return 摘要
     */
    public String digestHex(InputStream data) {
        return Hex.encodeHexString(digest(data));
    }

    /**
     * 生成摘要
     *
     * @param data         {@link InputStream} 数据流
     * @param bufferLength 缓存长度，不足1使用 {@link  com.chua.common.support.constant.CommonConstant#DEFAULT_BUFFER_SIZE} 做为默认值
     * @return 摘要bytes
     */
    public byte[] digest(InputStream data, int bufferLength) {
        return this.engine.digest(data, bufferLength);
    }

    /**
     * 生成摘要，并转为16进制字符串<br>
     * 使用默认缓存大小，见 {@link  com.chua.common.support.constant.CommonConstant#DEFAULT_BUFFER_SIZE}
     *
     * @param data         被摘要数据
     * @param bufferLength 缓存长度，不足1使用 {@link  com.chua.common.support.constant.CommonConstant#DEFAULT_BUFFER_SIZE} 做为默认值
     * @return 摘要
     */
    public String digestHex(InputStream data, int bufferLength) {
        return Hex.encodeHexString(digest(data, bufferLength));
    }

    /**
     * 验证生成的摘要与给定的摘要比较是否一致<br>
     * 简单比较每个byte位是否相同
     *
     * @param digest          生成的摘要
     * @param digestToCompare 需要比较的摘要
     * @return 是否一致
     * @see MessageDigest#isEqual(byte[], byte[])
     * @since 5.6.8
     */
    public boolean verify(byte[] digest, byte[] digestToCompare) {
        return MessageDigest.isEqual(digest, digestToCompare);
    }

    /**
     * 获取MAC算法块长度
     *
     * @return MAC算法块长度
     * @since 5.3.3
     */
    public int getMacLength() {
        return this.engine.getMacLength();
    }

    /**
     * 获取算法
     *
     * @return 算法
     * @since 5.3.3
     */
    public String getAlgorithm() {
        return this.engine.getAlgorithm();
    }
}
