package com.chua.common.support.utils;


import static com.chua.common.support.utils.ByteUtils.*;

/**
 * bit 工具类
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/3
 */
public class BitUtils {
    /**
     * bit -> 1
     */
    public static final int BIT_LENGTH = 8;
    /**
     * byte -> 1
     */
    public static final int BYTE_LENGTH = 1;
    public static final int BYTE_SIZE = BYTE_LENGTH;
    public static final int BYTE_BIT_SIZE = BIT_LENGTH;
    public static final int INT_SIZE = 4 * BYTE_SIZE;
    public static final int INT_BIT_SIZE = 4 * BYTE_BIT_SIZE;
    public static final int FLOAT_SIZE = INT_SIZE;
    public static final int LONG_SIZE = 8 * BYTE_SIZE;
    public static final int LONG_BIT_SIZE = 8 * BYTE_BIT_SIZE;
    public static final int DOUBLE_SIZE = LONG_SIZE;
    public static final int DOUBLE_BIT_SIZE = LONG_BIT_SIZE;
    public static final int SHORT_SIZE = 2 * BYTE_SIZE;
    public static final int SHORT_BIT_SIZE = 2 * BYTE_BIT_SIZE;
    public static final int CHAR_SIZE = SHORT_SIZE;
    public static final int CHAR_BIT_SIZE = SHORT_BIT_SIZE;
    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final String SYMBOL_BLANK = " ";

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static byte[] asBit(byte b) {
        byte[] byteArr = new byte[8];
        int size = BYTE_BIT_SIZE - 1;
        for (int i = size; i >= 0; i--) {
            //获取最低位
            byteArr[i] = (byte) (b & 0x01);
            //每次右移一位
            b = (byte) (b >> 1);
        }
        return byteArr;
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static byte[] asBit(byte[] b) {
        int length = b.length;
        if (length == BYTE_SIZE) {
            return asBit(b[0]);
        }
        if (length == SHORT_SIZE) {
            return asBit(toShort(b));
        }
        if (length == INT_SIZE) {
            return asBit(toInt(b));
        }

        if (length == LONG_SIZE) {
            return asBit(toLong(b));
        }

        byte[] result = new byte[length];
        if (length % LONG_SIZE == 0) {
            for (int i = 0; i < b.length; i += LONG_SIZE) {
                byte[] bytes = asBit(toLong(b));
                System.arraycopy(bytes, 0, result, i * LONG_SIZE, LONG_SIZE);
            }
        }

        if (length % INT_SIZE == 0) {
            for (int i = 0; i < b.length; i += INT_SIZE) {
                byte[] bytes = asBit(toInt(b));
                System.arraycopy(bytes, 0, result, i * INT_SIZE, INT_SIZE);
            }
        }

        if (length % SHORT_SIZE == 0) {
            for (int i = 0; i < b.length; i += SHORT_SIZE) {
                byte[] bytes = asBit(toShort(b));
                System.arraycopy(bytes, 0, result, i * SHORT_SIZE, SHORT_SIZE);
            }
        }

        for (int i = 0; i < b.length; i++) {
            byte[] bytes = asBit(b[i]);
            System.arraycopy(bytes, 0, result, i * BYTE_SIZE, BYTE_SIZE);

        }
        return result;
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static String asBitString(byte b) {
        byte[] bytes = asBit(b);
        return join(bytes, 8);
    }

    /**
     * 获取位数据
     *
     * @param source 字节
     * @return 位
     */
    public static String asBitString(String[] source) {
        return join(asStringBytes(source), 4);
    }

    /**
     * 获取位数据
     *
     * @param source 字节
     * @return 位
     */
    public static String asBitIntString(String source) {
        int length = source.length();
        if (length < INT_BIT_SIZE) {
            source = StringUtils.repeat("0", INT_BIT_SIZE - source.length()).concat(source);
        } else if (length > INT_BIT_SIZE) {
            source = source.substring(length - INT_BIT_SIZE, length);
        }
        return asBitString(source.split(""));
    }

    /**
     * 获取位数据
     *
     * @param source 字节
     * @return 位
     */
    public static String asBitLongString(String source) {
        int length = source.length();
        if (length < LONG_BIT_SIZE) {
            source = Strings.repeat("0", LONG_BIT_SIZE - source.length()).concat(source);
        } else if (length > LONG_BIT_SIZE) {
            source = source.substring(length - LONG_BIT_SIZE, length);
        }
        return asBitString(source.split(""));
    }

    /**
     * 获取位数据
     *
     * @param source 字节
     * @return 位
     */
    public static String asBitShortString(String source) {
        int length = source.length();
        if (length < SHORT_BIT_SIZE) {
            source = Strings.repeat("0", SHORT_BIT_SIZE - source.length()).concat(source);
        } else if (length > SHORT_BIT_SIZE) {
            source = source.substring(length - SHORT_BIT_SIZE, length);
        }
        return asBitString(source.split(""));
    }

    /**
     * 获取位数据
     *
     * @param source 字节
     * @return 位
     */
    public static String asBitByteString(String source) {
        int length = source.length();
        if (length < BYTE_BIT_SIZE) {
            source = Strings.repeat("0", BYTE_BIT_SIZE - source.length()).concat(source);
        } else if (length > BYTE_BIT_SIZE) {
            source = source.substring(length - BYTE_BIT_SIZE, length);
        }
        return asBitString(source.split(""));
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static byte[] asBit(short b) {
        byte[] bytes = asBytes(b);
        byte[] item = new byte[SHORT_BIT_SIZE];
        int index = 0;
        for (byte aByte : bytes) {
            byte[] bytes1 = asBit(aByte);
            System.arraycopy(bytes1, 0, item, index++ * 8, bytes1.length);
        }
        return item;
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static String asBitString(short b) {
        byte[] bytes = asBit(b);
        return join(bytes, 8);
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static byte[] asBit(long b) {
        byte[] bytes = asBytes(b);
        byte[] item = new byte[8 * 8];
        int index = 0;
        for (byte aByte : bytes) {
            byte[] bytes1 = asBit(aByte);
            System.arraycopy(bytes1, 0, item, index++ * 8, bytes1.length);
        }
        return item;
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static String asBitString(long b) {
        byte[] bytes = asBit(b);
        return join(bytes, 8);
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static byte[] asBit(float b) {
        byte[] bytes = asBytes(b);
        byte[] item = new byte[4 * 8];
        int index = 0;
        for (byte aByte : bytes) {
            byte[] bytes1 = asBit(aByte);
            System.arraycopy(bytes1, 0, item, index++ * 8, bytes1.length);
        }
        return item;
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static String asBitString(float b) {
        byte[] bytes = asBit(b);
        return join(bytes, 8);
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static byte[] asBit(double b) {
        byte[] bytes = asBytes(b);
        byte[] item = new byte[8 * 8];
        int index = 0;
        for (byte aByte : bytes) {
            byte[] bytes1 = asBit(aByte);
            System.arraycopy(bytes1, 0, item, index++ * 8, bytes1.length);
        }
        return item;
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static String asBitString(double b) {
        byte[] bytes = asBit(b);
        return join(bytes, 8);
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static byte[] asBit(int b) {
        byte[] bytes = asBytes(b);
        byte[] item = new byte[4 * 8];
        int index = 0;
        for (byte aByte : bytes) {
            byte[] bytes1 = asBit(aByte);
            System.arraycopy(bytes1, 0, item, index++ * 8, bytes1.length);
        }
        return item;
    }

    /**
     * 获取位数据
     *
     * @param b 字节
     * @return 位
     */
    public static String asBitString(int b) {
        byte[] bytes = asBit(b);
        return join(bytes, 8);
    }


    /**
     * 数组转字符串
     *
     * @param bytes 字节数组
     * @param size  每隔{size}位, 以空分隔
     * @return 字符串
     */
    public static String join(byte[] bytes, int size) {
        if (null == bytes || size < 0) {
            return "";
        }

        int sourceLength = bytes.length;
        StringBuilder stringBuilder = new StringBuilder();
        if (sourceLength <= size) {
            for (byte aByte : bytes) {
                stringBuilder.append(aByte);
            }
            return stringBuilder.toString();
        }

        int quotient = sourceLength / size;
        int mold = sourceLength % size;

        for (int i = 0; i < quotient; i++) {
            for (int j = i * size; j < size * (i + 1); j++) {
                byte aByte = bytes[j];
                stringBuilder.append(aByte);
            }
            stringBuilder.append(SYMBOL_BLANK);
        }

        if (mold != 0) {
            StringBuilder stringBuilder1 = new StringBuilder();
            for (int i = sourceLength; i > mold; i--) {
                stringBuilder1.append(bytes[i]);
            }
            stringBuilder.append(SYMBOL_BLANK).append(Strings.repeat("0", size - mold)).append(stringBuilder1);
        }
        String resource = stringBuilder.toString();
        return resource.endsWith(SYMBOL_BLANK) ? resource.substring(0, resource.length() - 1) : resource;
    }

    /**
     * 转封装类
     *
     * @param source 元数据
     * @return 封装类
     */
    private static byte[] asStringBytes(String[] source) {
        byte[] result = new byte[source.length];
        for (int i = 0; i < source.length; i++) {
            String b = source[i];
            result[i] = Byte.parseByte(b);
        }
        return result;
    }
}
