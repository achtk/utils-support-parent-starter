package com.chua.common.support.lang.net;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.utils.CharUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

/**
 * URLDecoder
 * @author CH
 */
public class UrlDecoder {
    private static final long serialVersionUID = 1L;

    private static final byte ESCAPE_CHAR = '%';

    /**
     * 解码，不对+解码
     *
     * <ol>
     *     <li>将%20转换为空格</li>
     *     <li>将 "%xy"转换为文本形式,xy是两位16进制的数值</li>
     *     <li>跳过不符合规范的%形式，直接输出</li>
     * </ol>
     *
     * @param str     包含URL编码后的字符串
     * @param charset 编码
     * @return 解码后的字符串
     */
    public static String decodeForPath(String str, Charset charset) {
        return decode(str, charset, false);
    }

    /**
     * 解码
     * <pre>
     *   1. 将+和%20转换为空格 ;
     *   2. 将"%xy"转换为文本形式,xy是两位16进制的数值;
     *   3. 跳过不符合规范的%形式，直接输出
     * </pre>
     *
     * @param str     包含URL编码后的字符串
     * @param charset 编码
     * @return 解码后的字符串
     */
    public static String decode(String str, Charset charset) {
        return decode(str, charset, true);
    }

    /**
     * 解码
     * <pre>
     *   1. 将%20转换为空格 ;
     *   2. 将"%xy"转换为文本形式,xy是两位16进制的数值;
     *   3. 跳过不符合规范的%形式，直接输出
     * </pre>
     *
     * @param str           包含URL编码后的字符串
     * @param isPlusToSpace 是否+转换为空格
     * @param charset       编码
     * @return 解码后的字符串
     */
    public static String decode(String str, Charset charset, boolean isPlusToSpace) {
        return StringUtils.str(decode(StringUtils.bytes(str, charset), isPlusToSpace), charset);
    }

    /**
     * 解码
     * <pre>
     *   1. 将+和%20转换为空格 ;
     *   2. 将"%xy"转换为文本形式,xy是两位16进制的数值;
     *   3. 跳过不符合规范的%形式，直接输出
     * </pre>
     *
     * @param bytes url编码的bytes
     * @return 解码后的bytes
     */
    public static byte[] decode(byte[] bytes) {
        return decode(bytes, true);
    }

    /**
     * 解码
     * <pre>
     *   1. 将%20转换为空格 ;
     *   2. 将"%xy"转换为文本形式,xy是两位16进制的数值;
     *   3. 跳过不符合规范的%形式，直接输出
     * </pre>
     *
     * @param bytes         url编码的bytes
     * @param isPlusToSpace 是否+转换为空格
     * @return 解码后的bytes
     * @since 5.6.3
     */
    public static byte[] decode(byte[] bytes, boolean isPlusToSpace) {
        if (bytes == null) {
            return null;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream(bytes.length);
        int b;
        for (int i = 0; i < bytes.length; i++) {
            b = bytes[i];
            if (b == '+') {
                buffer.write(isPlusToSpace ? CommonConstant.SYMBOL_SPACE_CHAR : b);
            } else if (b == ESCAPE_CHAR) {
                if (i + 1 < bytes.length) {
                    final int u = CharUtils.digit16(bytes[i + 1]);
                    if (u >= 0 && i + 2 < bytes.length) {
                        final int l = CharUtils.digit16(bytes[i + 2]);
                        if (l >= 0) {
                            buffer.write((char) ((u << 4) + l));
                            i += 2;
                            continue;
                        }
                    }
                }
                // 跳过不符合规范的%形式
                buffer.write(b);
            } else {
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }
}
