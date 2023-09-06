package com.chua.common.support.mapping.invoke.hik.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.chua.common.support.mapping.invoke.hik.constant.Constants.ENCODING;

/**
 * 消息摘要工具
 *
 * @author CH
 * @since 2023/09/06
 */
public class MessageDigestUtil {
    /**
     * base64和md5
     * 先进行MD5摘要再进行Base64编码获取摘要字符串
     *
     * @param str str
     * @return {@link String}
     */
    public static String base64AndMd5(String str) {
        if (str == null) {
            throw new IllegalArgumentException("inStr can not be null");
        }
        return base64AndMd5(toBytes(str)).trim();
    }

    /**
     * base64和md5
     * 先进行MD5摘要再进行Base64编码获取摘要字符串
     *
     * @param bytes 字节
     * @return {@link String}
     */
    public static String base64AndMd5(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes can not be null");
        }
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(bytes);
            final byte[] enbytes = Base64.getEncoder().encode(md.digest());
            return new String(enbytes);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("unknown algorithm MD5");
        }
    }

    /**
     * utf8至iso88591
     * UTF-8编码转换为ISO-9959-1
     *
     * @param str str
     * @return {@link String}
     */
    public static String utf8ToIso88591(String str) {
        if (str == null) {
            return null;
        }
        return new String(str.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

    /**
     * iso88591到utf8
     * ISO-9959-1编码转换为UTF-8
     *
     * @param str str
     * @return {@link String}
     */
    public static String iso88591ToUtf8(String str) {
        if (str == null) {
            return null;
        }

        return new String(str.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * 到字节
     * String转换为字节数组
     *
     * @param str str
     * @return {@link byte[]}
     */
    private static byte[] toBytes(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes(ENCODING);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
