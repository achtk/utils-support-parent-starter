package com.chua.common.support.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 字节工具类
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/26
 */
@Slf4j
public class ByteUtils extends BitUtils {

    /**
     * double value to byte array
     *
     * @param source double value
     * @return byte array
     */
    public static byte[] asBytes(double source) {
        try {
            return ByteBuffer.allocate(DOUBLE_SIZE).putDouble(source).array();
        } catch (Exception e) {
            long l = Double.doubleToRawLongBits(source);
            return asBytes(l);
        }
    }

    /**
     * float value to byte array
     *
     * @param source float value
     * @return byte array
     */
    public static byte[] asBytes(float source) {
        try {
            return ByteBuffer.allocate(FLOAT_SIZE).putFloat(source).array();
        } catch (Exception e) {
            int i = Float.floatToIntBits(source);
            return asBytes(i);
        }
    }

    /**
     * long value to byte array
     *
     * @param source short value
     * @return byte array
     */
    public static byte[] asBytes(long source) {
        try {
            return ByteBuffer.allocate(LONG_SIZE).putLong(source).array();
        } catch (Exception e) {
            byte[] result = new byte[LONG_SIZE];
            result[0] = (byte) ((source >> 56) & 0xFF);
            result[1] = (byte) ((source >> 48) & 0xFF);
            result[2] = (byte) ((source >> 40) & 0xFF);
            result[3] = (byte) ((source >> 32) & 0xFF);
            result[4] = (byte) ((source >> 24) & 0xFF);
            result[5] = (byte) ((source >> 16) & 0xFF);
            result[6] = (byte) ((source >> 8) & 0xFF);
            result[7] = (byte) (source & 0xFF);
            return result;
        }
    }

    /**
     * short value to byte array
     *
     * @param source short value
     * @return byte array
     */
    public static byte[] asBytes(short source) {
        try {
            return ByteBuffer.allocate(SHORT_SIZE).putShort(source).array();
        } catch (Exception e) {
            byte[] result = new byte[SHORT_SIZE];
            result[0] = (byte) ((source >> 8) & 0xFF);
            result[1] = (byte) (source & 0xFF);
            return result;
        }
    }

    /**
     * int to byte array
     *
     * @param source the int value
     * @return the byte array
     */
    public static byte[] asBytes(int source) {
        try {
            return ByteBuffer.allocate(INT_SIZE).putInt(source).array();
        } catch (Exception e) {
            byte[] result = new byte[INT_SIZE];
            result[0] = (byte) ((source >> 24) & 0xFF);
            result[1] = (byte) ((source >> 16) & 0xFF);
            result[2] = (byte) ((source >> 8) & 0xFF);
            result[3] = (byte) (source & 0xFF);
            return result;
        }

    }

    /**
     * byte array转char
     *
     * @param c char
     * @return byte[]
     */
    public static byte[] asBytes(char c) {
        try {
            return ByteBuffer.allocate(CHAR_SIZE).putChar(c).array();
        } catch (Exception e) {
            byte[] b = new byte[CHAR_SIZE];
            b[0] = (byte) ((c & 0xff00) >> 8);
            b[1] = (byte) (c & 0x00ff);
            return b;
        }
    }

    /**
     * double value to byte array
     *
     * @param source double value
     * @return byte array
     */
    public static byte[] asBytes(String source) {
        if (Strings.isNullOrEmpty(source)) {
            return EMPTY_BYTES;
        }
        return source.getBytes(UTF_8);
    }

    /**
     * 将boolean转成byte[]
     *
     * @param source 值
     * @return byte[]
     */
    public static byte[] asBytes(boolean source) {
        int tmp = !source ? 0 : 1;
        return ByteBuffer.allocate(INT_SIZE).putInt(tmp).array();
    }


    /**
     * 补码
     *
     * @param b 字节
     * @return 补码
     */
    public static byte[] complementArithmetic(int b) {
        int bit = INT_SIZE * BIT_LENGTH;
        List<String> split = Arrays.stream(asBitIntString(Integer.toBinaryString(-b)).split("")).filter(item -> !Strings.isNullOrEmpty(item)).collect(Collectors.toList());
        log.info("\n{}(源数据) \n-> {}(原码) \n-> {}(反码) \n-> {}(补码)", b, asBitIntString(Integer.toBinaryString(b)), asBitIntString(Integer.toBinaryString(~b)), asBitIntString(Joiner.on("").join(split)));
        byte[] bytes = new byte[bit];
        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);
            bytes[i] = Byte.valueOf(s);
        }
        return bytes;
    }

    /**
     * 补码
     *
     * @param b 字节
     * @return 补码
     */
    public static byte[] complementArithmetic(long b) {
        int bit = INT_SIZE * BIT_LENGTH;
        List<String> split = Arrays.stream(asBitLongString(Long.toBinaryString(-b)).split("")).filter(item -> !Strings.isNullOrEmpty(item)).collect(Collectors.toList());
        log.info("\n{}(源数据) \n-> {}(原码) \n-> {}(反码) \n-> {}(补码)", b, asBitLongString(Long.toBinaryString(b)), asBitLongString(Long.toBinaryString(~b)), asBitLongString(Joiner.on("").join(split)));
        byte[] bytes = new byte[bit];
        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);
            bytes[i] = Byte.valueOf(s);
        }
        return bytes;
    }

    /**
     * 补码
     *
     * @param b 字节
     * @return 补码
     */
    public static byte[] complementArithmetic(short b) {
        int bit = SHORT_SIZE * BIT_LENGTH;
        List<String> split = Arrays.stream(asBitShortString(Integer.toBinaryString(-b)).split("")).filter(item -> !Strings.isNullOrEmpty(item.trim())).collect(Collectors.toList());
        log.info("\n{}(源数据) \n-> {}(原码) \n-> {}(反码) \n-> {}(补码)", b, asBitShortString(Integer.toBinaryString(b)), asBitShortString(Integer.toBinaryString(~b)), asBitShortString(Joiner.on("").join(split)));
        byte[] bytes = new byte[bit];
        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);
            bytes[i] = Byte.valueOf(s);
        }
        return bytes;
    }

    /**
     * 补码
     *
     * @param b 字节
     * @return 补码
     */
    public static byte[] complementArithmetic(byte b) {
        int bit = BYTE_SIZE * BIT_LENGTH;
        List<String> split = Arrays.stream(asBitByteString(Integer.toBinaryString(-b)).split("")).filter(item -> !Strings.isNullOrEmpty(item)).collect(Collectors.toList());
        log.info("\n{}(源数据) \n-> {}(原码) \n-> {}(反码) \n-> {}(补码)", b, asBitByteString(Integer.toBinaryString(b)), asBitByteString(Integer.toBinaryString(~b)), asBitByteString(Joiner.on("").join(split)));
        byte[] bytes = new byte[bit];
        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);
            bytes[i] = Byte.valueOf(s);
        }
        return bytes;
    }

    /**
     * 补码值
     *
     * @param b 字节
     * @return 补码
     */
    public static BigInteger complementArithmeticValue(int b) {
        Byte[] bytes = asBytes(complementArithmetic(b));
        return BigInteger.valueOf(((b > Integer.MAX_VALUE || b < Integer.MIN_VALUE) ? -1 : 1) * Long.parseLong(Joiner.on("").join(bytes), 2));
    }

    /**
     * 补码值
     *
     * @param b 字节
     * @return 补码
     */
    public static BigInteger complementArithmeticValue(long b) {
        Byte[] bytes = asBytes(complementArithmetic(b));
        return BigInteger.valueOf(((b > Long.MAX_VALUE || b < Long.MIN_VALUE) ? -1 : 1) * Long.parseLong(Joiner.on("").join(bytes), 2));
    }

    /**
     * 补码值
     *
     * @param b 字节
     * @return 补码
     */
    public static BigInteger complementArithmeticValue(short b) {
        Byte[] bytes = asBytes(complementArithmetic(b));
        return BigInteger.valueOf(((b > Short.MAX_VALUE || b < Short.MIN_VALUE) ? -1 : 1) * Long.parseLong(Joiner.on("").join(bytes), 2));
    }

    /**
     * 补码值
     *
     * @param b 字节
     * @return 补码
     */
    public static BigInteger complementArithmeticValue(byte b) {
        Byte[] bytes = asBytes(complementArithmetic(b));
        return BigInteger.valueOf(Long.parseLong(Joiner.on("").join(bytes), 2));
    }

    /**
     * 类型转化<br />
     * 默认或者无效返回0
     *
     * @param bytes  字节数组
     * @param offset 位置
     * @param length 字节长度
     * @return 默认或者无效返回0
     */
    public static BigDecimal toBigDecimal(byte[] bytes, int offset, int length) {
        byte[] target = new byte[length];
        System.arraycopy(bytes, offset, target, 0, length);
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(target);
        buffer.flip();
        if (length == BYTE_SIZE) {
            return BigDecimal.valueOf(target[0]);
        } else if (length == SHORT_SIZE) {
            return BigDecimal.valueOf(buffer.getShort());
        } else if (length == INT_SIZE) {
            return BigDecimal.valueOf(buffer.getFloat());
        } else if (length == LONG_SIZE) {
            return BigDecimal.valueOf(buffer.getDouble());
        }
        return BigDecimal.ZERO;
    }

    /**
     * 将byte[]转成boolean
     *
     * @param bytes byte array
     * @return boolean
     */
    public static boolean toBoolean(byte[] bytes) {
        if (bytes == null) {
            return false;
        }
        byte[] item = new byte[INT_SIZE];
        int length = bytes.length;
        if (length < INT_SIZE) {
            System.arraycopy(bytes, 0, item, INT_SIZE - length, length);
        } else {
            item = bytes;
        }
        int tmp = ByteBuffer.wrap(item, 0, INT_SIZE).getInt();
        return tmp != 0;
    }

    /**
     * byte array to char value
     *
     * @param bytes byte array
     * @return char value
     */
    public static char toChar(byte[] bytes) {
        if (null == bytes) {
            throw new IndexOutOfBoundsException();
        }

        return ByteBuffer.wrap(createBytes(CHAR_SIZE, bytes), 0, CHAR_SIZE).getChar();
    }

    /**
     * byte array to char value
     *
     * @param bytes byte array
     * @return char value
     */
    public static char[] toChars(byte[] bytes) {
        if (null == bytes || bytes.length < CHAR_SIZE) {
            throw new IndexOutOfBoundsException();
        }
        return ByteBuffer.wrap(bytes).asCharBuffer().array();
    }

    /**
     * byte array to double value
     *
     * @param bytes byte array
     * @return double value
     */
    public static double toDouble(byte[] bytes) {
        if (null == bytes) {
            throw new IndexOutOfBoundsException();
        }
        return ByteBuffer.wrap(createBytes(DOUBLE_SIZE, bytes), 0, DOUBLE_SIZE).getDouble();

    }

    /**
     * byte array to float value
     *
     * @param bytes byte array
     * @return float value
     */
    public static float toFloat(byte[] bytes) {
        if (null == bytes) {
            throw new IndexOutOfBoundsException();
        }

        return ByteBuffer.wrap(createBytes(FLOAT_SIZE, bytes), 0, FLOAT_SIZE).getFloat();
    }

    /**
     * byte array to int value
     *
     * @param bytes byte array
     * @return int value
     */
    public static int toInt(byte[] bytes) {
        if (null == bytes) {
            throw new IndexOutOfBoundsException();
        }

        return ByteBuffer.wrap(createBytes(INT_SIZE, bytes), 0, INT_SIZE).getInt();
    }

    /**
     * byte array to long value
     *
     * @param bytes byte array
     * @return long value
     */
    public static long toLong(byte[] bytes) {
        if (null == bytes) {
            throw new IndexOutOfBoundsException();
        }
        return ByteBuffer.wrap(createBytes(LONG_SIZE, bytes), 0, LONG_SIZE).getLong();
    }

    /**
     * byte array to short value
     *
     * @param bytes byte array
     * @return short value
     */
    public static short toShort(byte[] bytes) {
        if (null == bytes) {
            throw new IndexOutOfBoundsException();
        }

        return ByteBuffer.wrap(createBytes(SHORT_SIZE, bytes), 0, SHORT_SIZE).getShort();
    }

    /**
     * 字节数组转为字符串
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    public static String toString(byte[] bytes) {
        return new String(bytes, UTF_8);
    }

    /**
     * 字节数组转为字符串
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    public static String toString(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }

    /**
     * 字节数组转为字符串
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    public static String toString(byte[] bytes, String charset) {
        return new String(bytes, Charset.forName(charset));
    }

    /**
     * 构建低位数组
     *
     * @param size  长度
     * @param bytes 源字节
     * @return 低位长度字节
     */
    private static byte[] createBytes(int size, byte[] bytes) {
        byte[] item = new byte[size];
        int length = bytes.length;
        if (length < size) {
            System.arraycopy(bytes, 0, item, size - length, length);
        } else if (length > size) {
            System.arraycopy(bytes, length - size, item, 0, size);
        } else {
            item = bytes;
        }
        return item;
    }

    /**
     * 转封装类
     *
     * @param source 元数据
     * @return 封装类
     */
    private static Byte[] asBytes(byte[] source) {
        Byte[] result = new Byte[source.length];
        for (int i = 0; i < source.length; i++) {
            byte b = source[i];
            result[i] = b;
        }
        return result;
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
        } else if (obj.getClass().isArray()) {
            return Joiner.on(",").join((Object[]) obj);
        }

        return obj.toString();
    }


    /**
     * 字节转Hex
     *
     * @param b 字节
     * @return hex
     */
    public static String asHex(byte b) {
        return Hex.encodeHexString(new byte[]{b});
    }

    /**
     * 字节数组转Hex
     *
     * @param bytes 字节数组
     * @return hex
     */
    public static String asHex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }


}
