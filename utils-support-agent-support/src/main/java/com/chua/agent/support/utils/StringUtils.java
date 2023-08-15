package com.chua.agent.support.utils;

import com.chua.agent.support.json.JSON;
import com.chua.agent.support.span.Span;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * string
 *
 * @author CH
 */
public class StringUtils {
    /**
     * {}
     */
    public static final String EMPTY_JSON = "{}";

    /**
     * \
     */
    public static final char SYMBOL_RIGHT_SLASH_CHAR = '\\';
    /**
     * {
     */
    public static final char SYMBOL_LEFT_BIG_PARANTHESES_CHAR = '{';

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT1 = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT2 = new SimpleDateFormat("HH:mm:ss");

    /**
     * 格式化字符串<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param strPattern 字符串模板
     * @param argArray   参数列表
     * @return 结果
     */
    public static String format(final String strPattern, final Object... argArray) {
        if (isNullOrEmpty(strPattern) || null == argArray || argArray.length == 0) {
            return strPattern;
        }
        final int strPatternLength = strPattern.length();
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);
        int handledPosition = 0;
        int delimindex;
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimindex = strPattern.indexOf(EMPTY_JSON, handledPosition);
            if (delimindex == -1) {
                // 不带占位符的模板直接返回
                if (handledPosition == 0) {
                    return strPattern;
                }
                // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                sbuf.append(strPattern, handledPosition, strPatternLength);
                return sbuf.toString();
            }

            // 转义符
            if (delimindex > 0 && strPattern.charAt(delimindex - 1) == SYMBOL_RIGHT_SLASH_CHAR) {
                // 双转义符
                if (delimindex > 1 && strPattern.charAt(delimindex - 2) == SYMBOL_RIGHT_SLASH_CHAR) {
                    // 转义符之前还有一个转义符，占位符依旧有效
                    sbuf.append(strPattern, handledPosition, delimindex - 1);
                    sbuf.append(utf8Str(argArray[argIndex]));
                    handledPosition = delimindex + 2;
                } else {
                    // 占位符被转义
                    argIndex--;
                    sbuf.append(strPattern, handledPosition, delimindex - 1);
                    sbuf.append(SYMBOL_LEFT_BIG_PARANTHESES_CHAR);
                    handledPosition = delimindex + 1;
                }
                // 正常占位符
            } else {
                sbuf.append(strPattern, handledPosition, delimindex);
                sbuf.append(utf8Str(createValue(argArray[argIndex])));
                handledPosition = delimindex + 2;
            }
        }

        // append the characters following the last {} pair.
        // 加入最后一个占位符后所有的字符
        sbuf.append(strPattern, handledPosition, strPattern.length());

        return sbuf.toString();
    }

    /**
     * 初始化值
     *
     * @param o 值
     * @return 值
     */
    private static Object createValue(Object o) {
        if (null == o) {
            return "NULL";
        }

        if (o instanceof String) {
            return "" + o + "";
        }

        if (o instanceof Date) {
            return "" + SIMPLE_DATE_FORMAT.format(o) + "";
        }


        if (o instanceof LocalDateTime) {
            return "" + SIMPLE_DATE_FORMAT.format(o) + "";
        }

        if (o instanceof LocalDate) {
            return "" + SIMPLE_DATE_FORMAT1.format((LocalDate) o) + "";
        }


        if (o instanceof LocalTime) {
            return "" + SIMPLE_DATE_FORMAT2.format(((LocalTime) o)) + "";
        }

        return o;
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String utf8Str(Object obj) {
        return str(obj, UTF_8);
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj     对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return str((byte[]) obj, charset);
        } else if (obj instanceof Byte[]) {
            return str(obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str(obj, charset);
        }

        return obj.toString();
    }

    /**
     * {@link CharSequence} 转为字符串，null安全
     *
     * @param cs {@link CharSequence}
     * @return 字符串
     */
    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    /**
     * 是否为空
     *
     * @param source 数据
     * @return 是否为空
     */
    public static boolean isNullOrEmpty(String source) {
        return null == source || source.length() == 0;
    }

    /**
     * 编码字符串
     *
     * @param str 字符串
     * @return 编码后的字节码
     */
    public static byte[] utf8Bytes(CharSequence str) {
        return bytes(str, UTF_8);
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, String charset) {
        return bytes(str, isNullOrEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 压缩
     *
     * @param toJSONBytes 字节
     * @return gzip
     */
    public static String gzip(byte[] toJSONBytes) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        ) {
            gzipOutputStream.write(toJSONBytes, 0, toJSONBytes.length);
            gzipOutputStream.finish();
            return new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray()), UTF_8);
        } catch (IOException e) {
            return new String(Base64.getEncoder().encode(toJSONBytes), UTF_8);
        }
    }

    public static List<Span> unGzip(String trim) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(trim.trim().getBytes(UTF_8)));
             GZIPInputStream gzipOutputStream = new GZIPInputStream(inputStream);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ) {
            byte[] buf = new byte[4096];
            int line = 0;
            while ((line = gzipOutputStream.read(buf)) > 0) {
                byteArrayOutputStream.write(buf, 0, line);
            }

            byte[] bytes = byteArrayOutputStream.toByteArray();
            return JSON.parseArray(new String(bytes), Span.class);
        } catch (Exception ignored) {
        }
        return Collections.emptyList();
    }
}
