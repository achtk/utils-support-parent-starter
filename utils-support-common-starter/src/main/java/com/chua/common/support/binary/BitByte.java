package com.chua.common.support.binary;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.utils.ByteUtils;
import com.chua.common.support.utils.Hex;
import com.chua.common.support.utils.StringUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.EMPTY;
import static com.chua.common.support.utils.ByteUtils.*;


/**
 * 字节/位
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/24
 */
public class BitByte {
    /**
     * 空值
     */
    private static final Pattern PATTERN_BLANK = Pattern.compile("\\s*|\t|\r|\n");
    /**
     * 空值
     */
    public static final Pattern PATTERN_EMPTY = Pattern.compile("\\s+");
    private static final Object LOCK = new Object();
    private byte[] bytes;

    protected BitByte(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * 添加字节
     *
     * @param bytes 字节
     * @return this
     */
    public BitByte addByte(byte... bytes) {
        synchronized (LOCK) {
            int length = this.bytes.length + bytes.length;
            byte[] bytes1 = new byte[length];
            System.arraycopy(this.bytes, 0, bytes1, 0, this.bytes.length);
            System.arraycopy(bytes, 0, bytes1, this.bytes.length, bytes.length);
            this.bytes = bytes1;
        }
        return this;
    }

    /**
     * 获取Hex
     *
     * @return Hex(0xFF 0x0C)
     */
    public String as0xHex() {
        return Hex.every0xSpace(Hex.encodeHexUpperString(bytes));
    }

    /**
     * 获取BigDecimal
     *
     * @return BigDecimal
     */
    public BigDecimal asBigDecimal() {
        return BigDecimal.valueOf(asDouble());
    }

    /**
     * 获取BigInteger
     *
     * @return BigInteger
     */
    public BigInteger asBigInteger() {
        return BigInteger.valueOf(asLong());
    }

    /**
     * 获取位
     *
     * @return byte[]
     */
    public byte[] asBit() {
        return ByteUtils.asBit(this.bytes);
    }

    /**
     * 获取boolean
     *
     * @return boolean
     */
    public boolean asBoolean() {
        return ByteUtils.toBoolean(this.bytes);
    }

    /**
     * 获取buffer
     *
     * @return ByteBuffer
     */
    public ByteBuffer asBuffer() {
        return ByteBuffer.wrap(bytes);
    }

    /**
     * 获取字节
     *
     * @return 字节
     */
    public byte[] asBytes() {
        return this.bytes;
    }

    /**
     * 获取 float<p>低位</p>
     *
     * @return float
     */
    public double asDouble() {
        return ByteUtils.toDouble(this.bytes);
    }

    /**
     * 获取 float<p>低位</p>
     *
     * @return float
     */
    public float asFloat() {
        return ByteUtils.toFloat(this.bytes);
    }

    /**
     * 获取 int<p>低位</p>
     *
     * @return int
     */
    public int asInt() {
        return ByteUtils.toInt(this.bytes);
    }

    /**
     * 获取 long <p>低位</p>
     *
     * @return long
     */
    public long asLong() {
        return ByteUtils.toLong(this.bytes);
    }

    /**
     * 补码
     *
     * @return 补码
     */
    public String asOneComplementArithmeticCode() {
        int length = bytes.length;
        if (length == BYTE_SIZE) {
            byte aByte = bytes[0];
            String code;
            if (aByte > Byte.MAX_VALUE || aByte < Byte.MIN_VALUE) {
                code = Integer.toBinaryString(-aByte);
            } else {
                code = Integer.toBinaryString(aByte);
            }
            return ByteUtils.asBitByteString(code);
        }

        if (length == SHORT_SIZE) {
            int aShort = asInt();
            String code;
            if (aShort > Short.MAX_VALUE || aShort < Short.MIN_VALUE) {
                code = Integer.toBinaryString(-aShort);
            } else {
                code = Integer.toBinaryString(aShort);
            }
            return ByteUtils.asBitShortString(code);
        }

        if (inRange(length, INT_SIZE, SHORT_SIZE)) {
            long aInt = asLong();
            String code;
            if (aInt > Integer.MAX_VALUE || aInt < Integer.MIN_VALUE) {
                code = Long.toBinaryString(-aInt);
            } else {
                code = Long.toBinaryString(aInt);
            }
            return ByteUtils.asBitIntString(code);
        }

        if (inRange(length, LONG_SIZE, INT_SIZE)) {
            long aLong = asLong();
            String code;
            if (aLong > Long.MAX_VALUE || aLong < Long.MIN_VALUE) {
                code = Long.toBinaryString(-aLong);
            } else {
                code = Long.toBinaryString(aLong);
            }
            return ByteUtils.asBitLongString(code);
        }
        return "";
    }

    /**
     * 补码值
     *
     * @return 补码值
     */
    public BigInteger asOneComplementArithmeticCodeValue() {

        String value = replaceTrim(asOneComplementArithmeticCode());
        long aLong = Long.parseLong(value, 2);
        if (value.length() == BYTE_BIT_SIZE) {
            return BigInteger.valueOf(aLong);
        }
        if (value.length() == SHORT_BIT_SIZE) {
            return BigInteger.valueOf(aLong);
        }

        if (value.length() == INT_BIT_SIZE) {
            return BigInteger.valueOf(aLong);
        }

        if (value.length() == LONG_BIT_SIZE) {
            return BigInteger.valueOf(aLong);
        }
        return BigInteger.ZERO;
    }

    /**
     * 页面中去除字符串中的空格、回车、换行符、制表符
     * <pre>
     *     replaceBlank("23") = "23"
     *     replaceBlank("23\t") = "23"
     *     replaceBlank("23\n") = "23"
     *     replaceBlank("23\r") = "23"
     *     replaceBlank("23 ") = "23"
     *     replaceBlank("2 3 ") = "23"
     *     replaceBlank(null) = ""
     * </pre>
     *
     * @param str 需要处理的字符串
     */
    public String replaceTrim(String str) {
        if (StringUtils.isEmpty(str)) {
            return EMPTY;
        }
        Matcher m = PATTERN_BLANK.matcher(str);
        return m.replaceAll(EMPTY);
    }

    /**
     * 反码
     *
     * @return 反码
     */
    public String asOneInverseArithmetic() {
        int length = bytes.length;
        if (length == BYTE_SIZE) {
            byte aByte = bytes[0];
            String code;
            code = Integer.toBinaryString(aByte);
            return ByteUtils.asBitByteString(code);
        }

        if (length == SHORT_SIZE) {
            int aShort = asInt();
            String code;
            if (aShort > Short.MAX_VALUE || aShort < Short.MIN_VALUE) {
                code = Integer.toBinaryString(~aShort);
            } else {
                code = Integer.toBinaryString(aShort);
            }
            return ByteUtils.asBitShortString(code);
        }

        if (inRange(length, INT_SIZE, SHORT_SIZE)) {
            long aInt = asLong();
            String code;
            if (aInt > Integer.MAX_VALUE || aInt < Integer.MIN_VALUE) {
                code = Long.toBinaryString(~aInt);
            } else {
                code = Long.toBinaryString(aInt);
            }
            return ByteUtils.asBitIntString(code);
        }

        if (inRange(length, LONG_SIZE, INT_SIZE)) {
            long aLong = asLong();
            String code;
            if (aLong > Long.MAX_VALUE || aLong < Long.MIN_VALUE) {
                code = Long.toBinaryString(~aLong);
            } else {
                code = Long.toBinaryString(aLong);
            }
            return ByteUtils.asBitLongString(code);
        }
        return "";
    }

    /**
     * 反码值
     *
     * @return 反码值
     */
    public BigInteger asOneInverseArithmeticValue() {
        String value = replaceTrim(asOneInverseArithmetic());
        int length = value.length();
        long aLong = Long.parseLong(value, 2);
        if (length == BYTE_BIT_SIZE) {
            return BigInteger.valueOf(aLong);
        }
        if (length == SHORT_BIT_SIZE) {
            return BigInteger.valueOf(aLong);
        }

        if (length == INT_BIT_SIZE) {
            return BigInteger.valueOf(aLong);
        }

        if (length == LONG_BIT_SIZE) {
            long asLong = asLong();
            return BigInteger.valueOf(((asLong > Long.MAX_VALUE || asLong < Long.MIN_VALUE) ? -1 : 1) * aLong);
        }
        return BigInteger.ZERO;
    }

    /**
     * 原码
     *
     * @return 原码
     */
    public String asOneOriginalCode() {
        int length = bytes.length;
        if (length == BYTE_SIZE) {
            return ByteUtils.asBitByteString(Integer.toBinaryString(bytes[0]));
        }

        if (length == SHORT_SIZE) {
            return ByteUtils.asBitShortString(Integer.toBinaryString(asShort()));
        }

        if (length == INT_SIZE) {
            return ByteUtils.asBitIntString(Integer.toBinaryString(asInt()));
        }

        if (length == LONG_SIZE) {
            return ByteUtils.asBitLongString(Long.toBinaryString(asLong()));
        }

        if (length < INT_SIZE && length > SHORT_SIZE) {
            return ByteUtils.asBitIntString(Integer.toBinaryString(asInt()));
        }

        if (length < LONG_SIZE && length > INT_SIZE) {
            return ByteUtils.asBitLongString(Long.toBinaryString(asLong()));
        }

        return "";
    }

    /**
     * 原码值
     *
     * @return 原码值
     */
    public Long asOneOriginalCodeValue() {
        return Long.parseLong(replaceTrim(asOneOriginalCode()), 2);
    }

    /**
     * 获取 short<p>低位</p>
     *
     * @return short
     */
    public short asShort() {
        return ByteUtils.toShort(this.bytes);
    }

    /**
     * 插入字节
     *
     * @param index 索引
     * @param b     字节
     * @return this
     */
    public BitByte insertByte(int index, byte b) {
        synchronized (LOCK) {
            int length = this.bytes.length + 1;
            byte[] bytes1 = new byte[length];
            System.arraycopy(this.bytes, 0, bytes1, 0, index);
            System.arraycopy(new byte[]{b}, 0, bytes1, index, 1);
            if (this.bytes.length > index) {
                System.arraycopy(this.bytes, index, bytes1, index + 1, length - index - 1);
            }
            this.bytes = bytes1;
        }
        return this;
    }

    /**
     * 删除字节
     *
     * @param index 索引
     * @return this
     */
    public byte remove(int index) {
        synchronized (LOCK) {
            int length = this.bytes.length - 1;
            byte[] bytes1 = new byte[length];
            System.arraycopy(this.bytes, 0, bytes1, 0, index);
            System.arraycopy(this.bytes, index + 1, bytes1, index, length - index);
            byte source = (byte) Array.get(bytes, index);
            this.bytes = bytes1;
            return source;
        }
    }

    @Override
    public String toString() {
        return asOneOriginalCode();
    }

    public static BitByte of(int value) {
        return new BitByte(ByteUtils.asBytes(value));
    }

    public static BitByte of(boolean value) {
        return new BitByte(ByteUtils.asBytes(value));
    }

    public static BitByte of(String hex, Pattern delimiter) throws Exception {
        return of(Splitter.on(delimiter).omitEmptyStrings().trimResults().splitToList(hex));
    }

    public static BitByte of(String hex, String delimiter) throws Exception {
        return of(Splitter.on(delimiter).omitEmptyStrings().trimResults().splitToList(hex));
    }

    public static BitByte of(String hex) throws Exception {
        return of(Splitter.on(PATTERN_EMPTY).omitEmptyStrings().trimResults().splitToList(hex));
    }

    public static BitByte of(String... hexes) throws Exception {
        return of(Arrays.asList(hexes));
    }

    public static BitByte of(List<String> hexes) throws Exception {
        StringBuffer newHex = new StringBuffer();
        //空格分隔
        for (String hex : hexes) {
            if (hex.length() == 1) {
                newHex.append("0").append(hex);
            } else {
                newHex.append(hex, 0, 2);
            }
        }
        return new BitByte(Hex.decodeHex(newHex.toString()));
    }

    public static BitByte of(short value) {
        return new BitByte(ByteUtils.asBytes(value));
    }

    public static BitByte of(long value) {
        return new BitByte(ByteUtils.asBytes(value));
    }

    public static BitByte of(float value) {
        return new BitByte(ByteUtils.asBytes(value));
    }

    public static BitByte of(double value) {
        return new BitByte(ByteUtils.asBytes(value));
    }

    /**
     * 在区间之中
     *
     * @param length 长度
     * @param max    最大值
     * @param min    最小值
     * @return 满足条件true
     */
    private boolean inRange(int length, int max, int min) {
        return length == max || (length < max && length > min);
    }
}
