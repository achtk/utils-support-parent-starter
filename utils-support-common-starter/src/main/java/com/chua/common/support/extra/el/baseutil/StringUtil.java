package com.chua.common.support.extra.el.baseutil;

import com.chua.common.support.extra.el.baseutil.exception.UnSupportException;
import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 基础类
 *
 * @author CH
 */
public class StringUtil {
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 将byte数组以16进制的形式输出
     *
     * @param src
     * @return
     */
    public static String toHexString(byte[] src) {
        char[] result = new char[src.length * 2];
        for (int i = 0; i < src.length; i++) {
            result[i * 2] = DIGITS_LOWER[(src[i] & 0xf0) >>> 4];
            result[i * 2 + 1] = DIGITS_LOWER[src[i] & 0x0f];
        }
        return new String(result);
    }

    public static String toHexString(byte[] src, int off, int length) {
        char[] result = new char[length * 2];
        length = off + length;
        for (int i = off; i < length; i++) {
            result[i * 2] = DIGITS_LOWER[(src[i] & 0xf0) >>> 4];
            result[i * 2 + 1] = DIGITS_LOWER[src[i] & 0x0f];
        }
        return new String(result);
    }

    /**
     * 将hex字符的字符串转变byte数组
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexStringToBytes(String hexStr) {
        return hexCharsToBytes(hexStr.toLowerCase().toCharArray());
    }

    /**
     * 将hex字符的char数组转变byte数组
     *
     * @param hexChars
     * @return
     */
    public static byte[] hexCharsToBytes(char[] hexChars) {
        if ((hexChars.length & 0x01) == 1) {
            throw new UnSupportException("用于解析的十六进制字符数组的长度不对，不是2的整数倍");
        }
        int length = hexChars.length / 2;
        byte[] result = new byte[length];
        for (int i = 0; i < hexChars.length; i += 2) {
            int f = toDigit(hexChars[i]) << 4;
            f = f | toDigit(hexChars[i + 1]);
            result[i >> 1] = (byte) f;
        }
        return result;
    }

    private static int toDigit(char c) {
        int index = 0;
        for (; index < 16; index++) {
            if (DIGITS_LOWER[index] == c) {
                return index;
            }
        }
        throw new UnSupportException("字符" + c + "不是小写十六进制的字符");
    }

    /**
     * 使用匹配规则检测字符串，如果匹配返回true 匹配规则为，如果有*则认为可以是任意字符，从前到后匹配
     *
     * @param src  需要检测的字符串
     * @param rule 匹配规则
     * @return
     */
    public static boolean match(String src, String rule) {
        if (rule.contains("*")) {
            String[] tmps = rule.split("\\*");
            int index = 0;
            for (int i = 0; i < tmps.length; i++) {
                // 从前往后匹配，每一次匹配成功，将index增加，这样就可以去匹配的字符串
                index = src.indexOf(tmps[i], index);
                if (index >= 0) {
                    index += tmps[i].length();
                } else {
                    break;
                }
            }
            // 不匹配，返回false
            if (index == -1) {
                return false;
            }
            // 如果结尾不是*号，则匹配完毕必然是index==src.length。如果只有*，则index=0.
            else if (index == src.length() || index == 0) {
                return true;
            }
            // 如果index比src的长度小，又不是0.那么如果rule中*是最后一个字母，则表示此时匹配，返回true
            else {
                return rule.charAt(rule.length() - 1) == '*';
            }
        } else {
            return src.equals(rule);
        }
    }

    /**
     * 检测指定的src是否不为空。不为空返回true
     *
     * @param src
     * @return
     */
    public static boolean isNotBlank(String src) {
        return src != null && "".equals(src) != true;
    }

    /**
     * @param src
     * @return
     */
    public static boolean isBlank(String src) {
        return src == null || "".equals(src);
    }

    /**
     * 对字符串进行参数占位符替换。比如pattern是“你好我是{}”,使用后面的参数替换掉{}. 替换的顺序和参数自身的顺序一致
     *
     * @param pattern
     * @param params
     * @return
     */
    public static String format(String pattern, Object... params) {
        StringBuilder cache = new StringBuilder();
        char[] value = pattern.toCharArray();
        int total = params.length;
        int start = 0;
        int pre = 0;
        for (int i = 0; i < total; i++) {
            start = indexOfBrace(value, pre);
            if (start == -1) {
                cache.append(value, pre, value.length - pre);
                return cache.toString();
            } else {
                cache.append(value, pre, start - pre);
                cache.append(params[i]);
                pre = start + 2;
            }
        }
        cache.append(value, pre, value.length - pre);
        return cache.toString();
    }

    public static String format(String messageTemplate, Map<String, String> values) {
        StringBuilder cache = new StringBuilder();
        cache.setLength(0);
        char[] charArray = messageTemplate.toCharArray();
        int start;
        int end = 0;
        while (true) {
            start = indexOf('{', end, charArray);
            if (start != -1) {
                cache.append(charArray, end, start - end);
                end = indexOf('}', start, charArray);
                if (end != -1) {
                    String name = messageTemplate.substring(start + 1, end);
                    cache.append(values.get(name));
                    end += 1;
                } else {
                    cache.append(charArray, end, charArray.length - end);
                    break;
                }
            } else {
                cache.append(charArray, end, charArray.length - end);
                break;
            }
        }
        return cache.toString();
    }

    private static int indexOf(char c, int index, char[] array) {
        for (int i = index; i < array.length; i++) {
            if (array[i] == c) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 从char数组中确定大括号的位置，如果不存在返回-1
     *
     * @param array
     * @param off
     * @return
     */
    private static int indexOfBrace(char[] array, int off) {
        int length = array.length - 1;
        for (int i = off; i < length; i++) {
            if (array[i] == '{' && array[i + 1] == '}') {
                return i;
            }
        }
        return -1;
    }

    /**
     * 得到字符串的内存字节表示
     *
     * @param str
     * @return
     */
    public static String getHexBytes(String str) {
        char[] array = str.toLowerCase().toCharArray();
        int length = array.length;
        byte[] tmp = new byte[length * 2];
        char c;
        int index = 0;
        for (int i = 0; i < length; i++) {
            c = array[i];
            tmp[index++] = (byte) (c >>> 8);
            tmp[index++] = (byte) c;
        }
        return toHexString(tmp);
    }

    public static String readFromClasspath(String resName, Charset charset) {
        InputStream inputStream = null;
        try {
            inputStream = StringUtil.class.getClassLoader().getResourceAsStream(resName);
            byte[] src = new byte[inputStream.available()];
            inputStream.read(src);
            return new String(src, charset);
        } catch (IOException e) {
            ReflectUtil.throwException(e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(getHexBytes("你好"));
    }
}
