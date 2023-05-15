package com.chua.common.support.utils;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * Hex<br />
 * 部分工具来自于HuTool系列
 *
 * @author CH
 * @version 1.0.0
 */
public class Hex {
    /**
     * 0x01
     */
    public static final int HEX_0X01 = 0x01;
    /**
     * Used to build output as hex.
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * Used to build output as hex.
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final int TWE = 2;
    private final Charset charset = StandardCharsets.UTF_8;

    /**
     * byte to hex string
     *
     * @param bytes byte array
     * @return hex string
     */
    public static String toHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * hex加密
     *
     * @param data 数据
     * @return String
     */
    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    /**
     * hex加密
     *
     * @param data 数据
     * @return String
     */
    public static String encodeHexUpperString(final byte[] data) {
        return new String(encodeHex(data, false));
    }

    /**
     * hex加密
     *
     * @param data 数据
     * @return char[]
     */
    public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }

    /**
     * hex加密
     *
     * @param data     数据
     * @param toDigits char[]
     * @return char[]
     */
    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        encodeHex(data, 0, data.length, toDigits, out, 0);
        return out;
    }

    /**
     * hex加密
     *
     * @param data       代加密字节数组
     * @param dataOffset 字节数组起始位置
     * @param dataLen    字节数组加密长度
     * @param toDigits   加密字符
     * @param out        输出结果字符数组
     * @param outOffset  字符数组输出位置
     */
    private static void encodeHex(final byte[] data, final int dataOffset, final int dataLen, final char[] toDigits, final char[] out, final int outOffset) {
        for (int i = dataOffset, j = outOffset; i < dataOffset + dataLen; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
    }

    /**
     * hex解码
     *
     * @param data 数据
     * @return byte数组
     * @throws Exception Exception
     */
    public static byte[] decodeHex(final char[] data) throws Exception {
        final byte[] out = new byte[data.length >> 1];
        decodeHex(data, out, 0);
        return out;
    }

    /**
     * hex解码
     *
     * @param data 数据
     * @return byte数组
     * @throws Exception Exception
     */
    public static byte[] decodeHex(final String data) throws Exception {
        return decodeHex(data.toCharArray());
    }

    /**
     * hex加密
     *
     * @param data        数据
     * @param toLowerCase 是否小写
     * @return char[]
     */
    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * hex转原始字符串(包含中文)
     *
     * @param hex     hex数据
     * @param charset 编码
     * @return 原始字符串
     */
    public static String convertHexToString(final String hex, final Charset charset) {
        StringBuilder sb = new StringBuilder();
        List<Byte> tempCache = new ArrayList<>();

        for (int i = 0; i < hex.length() - 1; i += TWE) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            if (decimal > 0 && decimal < 128) {
                sb.append((char) decimal);
                continue;
            }
            byte value = Integer.valueOf(decimal).byteValue();
            tempCache.add(value);
            if (tempCache.size() == 2) {
                sb.append(getStringByCharset(tempCache, charset));
            }
        }
        return sb.toString();
    }

    /**
     * 通过编码转中文
     *
     * @param tempCache 缓存字节
     * @param charset   编码
     * @return 字符串
     */
    private static String getStringByCharset(List<Byte> tempCache, Charset charset) {
        byte[] bytes = new byte[tempCache.size()];
        for (int i = 0; i < tempCache.size(); i++) {
            bytes[i] = tempCache.get(i);
        }
        tempCache.clear();
        return new String(bytes, charset);
    }

    /**
     * 转化为Hex(包含中文)
     *
     * @param source  元数据
     * @param charset 处理中文时编码
     * @return String
     */
    public static String convertStringToHex(final String source, final Charset charset) {
        char[] chars = source.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            String string = Integer.toHexString(aChar);
            //中文
            if (string.length() == 4) {
                hex.append(getHexByCharset(aChar, charset));
                continue;
            }
            hex.append(string.length() == 1 ? "0" + string : string);
        }
        return hex.toString();
    }

    /**
     * 通过编码获取中文 HEX
     *
     * @param aChar   中文
     * @param charset 编码
     * @return 中文 HEX
     */
    private static String getHexByCharset(char aChar, Charset charset) {
        String str1 = String.valueOf(aChar);
        byte[] bytes = str1.getBytes(charset);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            sb.append(DIGITS_LOWER[(aByte & 0xf0) >> 4]);
            sb.append(DIGITS_LOWER[(aByte & 0x0f)]);
        }
        return sb.toString();
    }

    /**
     * hex解码
     *
     * @param data      数据
     * @param out       输出字节数组
     * @param outOffset 输出位置
     * @return int
     * @throws Exception Exception
     */
    public static int decodeHex(final char[] data, final byte[] out, final int outOffset) throws Exception {
        final int len = data.length;

        if ((len & HEX_0X01) != 0) {
            throw new Exception("Odd number of characters.");
        }

        final int outLen = len >> 1;
        if (out.length - outOffset < outLen) {
            throw new Exception("Output array is not large enough to accommodate decoded data.");
        }

        //hex值转为两个字符
        for (int i = outOffset, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return outLen;
    }

    /**
     * 转为数字
     *
     * @param ch    char
     * @param index 索引
     * @return 数字
     * @throws Exception Exception
     */
    protected static int toDigit(final char ch, final int index) throws Exception {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    /**
     * byteBuffer 转 byte[]
     *
     * @param byteBuffer byteBuffer
     * @return byte[]
     */
    private static byte[] toByteArray(final ByteBuffer byteBuffer) {
        final int remaining = byteBuffer.remaining();
        if (byteBuffer.hasArray()) {
            final byte[] byteArray = byteBuffer.array();
            if (remaining == byteArray.length) {
                byteBuffer.position(remaining);
                return byteArray;
            }
        }
        final byte[] byteArray = new byte[remaining];
        byteBuffer.get(byteArray);
        return byteArray;
    }

    /**
     * Hex数据间隔
     *
     * @param hex Hex
     * @return 间隔的Hex
     */
    public static String everySpace(String hex) {
        return everySpace(hex, 2);
    }

    /**
     * Hex数据间隔
     *
     * @param hex  Hex
     * @param size 间隔长度
     * @return 间隔的Hex
     */
    public static String everySpace(String hex, int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += size) {
            String output = hex.substring(i, (i + size));
            sb.append(" ").append(output);
        }
        return sb.length() > 0 ? sb.substring(1) : sb.toString();
    }

    /**
     * Hex数据间隔
     *
     * @param hex Hex
     * @return 间隔的Hex
     */
    public static String every0xSpace(String hex) {
        return every0xSpace(hex, 2);
    }

    /**
     * Hex数据间隔
     *
     * @param hex  Hex
     * @param size 间隔长度
     * @return 间隔的Hex
     */
    public static String every0xSpace(String hex, int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += size) {
            String output = hex.substring(i, (i + size));
            sb.append(" 0x").append(output);
        }
        return sb.length() > 0 ? sb.substring(1) : sb.toString();
    }

    /**
     * 将byte值转为16进制并添加到{@link StringBuilder}中
     *
     * @param builder     {@link StringBuilder}
     * @param b           byte
     * @param toLowerCase 是否使用小写
     * @since 4.4.1
     */
    public static void appendHex(StringBuilder builder, byte b, boolean toLowerCase) {
        final char[] toDigits = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
        //高位
        int high = (b & 0xf0) >>> 4;
        //低位
        int low = b & 0x0f;
        builder.append(toDigits[high]);
        builder.append(toDigits[low]);
    }

    public byte[] encode(byte[] source) throws Exception {
        return encodeHexString(source).getBytes(this.getCharset());
    }

    public Object decode(Object object) throws Exception {
        if (object instanceof String) {
            return decode(((String) object).toCharArray());
        } else if (object instanceof byte[]) {
            return decode((byte[]) object);
        } else if (object instanceof ByteBuffer) {
            return decode(object);
        } else {
            try {
                return decodeHex((char[]) object);
            } catch (final ClassCastException e) {
                throw new Exception(e.getMessage(), e);
            }
        }
    }

    public Object encode(Object object) throws Exception {
        byte[] byteArray;
        if (object instanceof String) {
            byteArray = ((String) object).getBytes(this.getCharset());
        } else if (object instanceof ByteBuffer) {
            byteArray = toByteArray((ByteBuffer) object);
        } else {
            try {
                byteArray = (byte[]) object;
            } catch (final ClassCastException e) {
                throw new Exception(e.getMessage(), e);
            }
        }
        return encodeHex(byteArray);
    }

    public byte[] decode(final byte[] array) throws Exception {
        return decodeHex(new String(array, getCharset()).toCharArray());
    }

    /**
     * Gets the charset.
     *
     * @return the charset.
     * @since 1.7
     */
    public Charset getCharset() {
        return this.charset;
    }
}
