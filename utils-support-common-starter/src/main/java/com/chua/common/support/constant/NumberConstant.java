package com.chua.common.support.constant;

import java.io.File;
import java.math.BigInteger;

/**
 * 数字常量
 *
 * @author CH
 */
public interface NumberConstant {
    /**
     * The number of bytes in a kilobyte.
     */
    long ONE_KB = 1024;

    /**
     * The number of bytes in a kilobyte.
     *
     * @since 2.4
     */
    BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);

    /**
     * The number of bytes in a megabyte.
     */
    long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a megabyte.
     *
     * @since 2.4
     */
    BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

    /**
     * The number of bytes in a gigabyte.
     */
    long ONE_GB = ONE_KB * ONE_MB;

    /**
     * The number of bytes in a gigabyte.
     *
     * @since 2.4
     */
    BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

    /**
     * The number of bytes in a terabyte.
     */
    long ONE_TB = ONE_KB * ONE_GB;

    /**
     * The number of bytes in a terabyte.
     *
     * @since 2.4
     */
    BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

    /**
     * The number of bytes in a petabyte.
     */
    long ONE_PB = ONE_KB * ONE_TB;

    /**
     * The number of bytes in a petabyte.
     *
     * @since 2.4
     */
    BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

    /**
     * The number of bytes in an exabyte.
     */
    long ONE_EB = ONE_KB * ONE_PB;

    /**
     * The number of bytes in an exabyte.
     *
     * @since 2.4
     */
    BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    /**
     * The number of bytes in a zettabyte.
     */
    BigInteger ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));

    /**
     * The number of bytes in a yottabyte.
     */
    BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

    /**
     * An empty array of type {@code File}.
     */
    File[] EMPTY_FILE_ARRAY = {};
    /**
     * 默认除法运算精度
     */
    int DEFAUT_DIV_SCALE = 10;
    /**
     * Reusable Long constant for zero.
     */
    Long LONG_ZERO = 0L;
    /**
     * Reusable Long constant for one.
     */
    Long LONG_ONE = 1L;
    /**
     * Reusable Long constant for minus one.
     */
    Long LONG_MINUS_ONE = -1L;
    /**
     * Reusable Integer constant for zero.
     */
    Integer INTEGER_ZERO = 0;
    /**
     * Reusable Integer constant for one.
     */
    Integer INTEGER_ONE = 1;
    /**
     * Reusable Integer constant for two
     */
    Integer INTEGER_TWO = 2;
    /**
     * Reusable Integer constant for minus one.
     */
    Integer INTEGER_MINUS_ONE = -1;
    /**
     * Reusable Short constant for zero.
     */
    Short SHORT_ZERO = (short) 0;
    /**
     * Reusable Short constant for one.
     */
    Short SHORT_ONE = (short) 1;
    /**
     * Reusable Short constant for minus one.
     */
    Short SHORT_MINUS_ONE = (short) -1;
    /**
     * Reusable Byte constant for zero.
     */
    Byte BYTE_ZERO = (byte) 0;
    /**
     * Reusable Byte constant for one.
     */
    Byte BYTE_ONE = (byte) 1;
    /**
     * Reusable Byte constant for minus one.
     */
    Byte BYTE_MINUS_ONE = (byte) -1;
    /**
     * Reusable Double constant for zero.
     */
    Double DOUBLE_ZERO = 0.0d;
    /**
     * Reusable Double constant for one.
     */
    Double DOUBLE_ONE = 1.0d;
    /**
     * Reusable Double constant for minus one.
     */
    Double DOUBLE_MINUS_ONE = -1.0d;
    /**
     * Reusable Float constant for zero.
     */
    Float FLOAT_ZERO = 0.0f;
    /**
     * Reusable Float constant for one.
     */
    Float FLOAT_ONE = 1.0f;
    /**
     * Reusable Float constant for minus one.
     */
    Float FLOAT_MINUS_ONE = -1.0f;

    int STRING_BUILDER_SIZE = 256;

    int INDEX_NOT_FOUND = -1;

    int PAD_LIMIT = 8192;
    /**
     * 初始化大小
     */
    int DEFAULT_SIZE = 1 << 4;

    /**
     * -1
     */
    int EOF = INDEX_NOT_FOUND;

    int SKIP_BUFFER_SIZE = 2048;
    /**
     * 1
     */
    int ONE = 1;
    /**
     * 1
     */
    int FIRST = ONE;
    /**
     * -2
     */
    int NEGATIVE_TWE = -2;
    /**
     * 2
     */
    int TWE = 2;
    /**
     * 2
     */
    int SECOND = TWE;
    /**
     * 3
     */
    int THREE = 3;
    /**
     * 3
     */
    int THIRD = THREE;
    /**
     * 4
     */
    int FOUR = 4;
    /**
     * 4
     */
    int FOURTH = FOUR;
    /**
     * 5
     */
    int FIVE = 5;
    /**
     * 5
     */
    int FIFTH = FIVE;
    /**
     * 6
     */
    int SIX = 6;
    /**
     * 6
     */
    int SIXTH = SIX;
    /**
     * 7
     */
    int SEVEN = 7;
    /**
     * 7
     */
    int SEVENTH = SEVEN;
    /**
     * 8
     */
    int EIGHT = 8;
    /**
     * 8
     */
    int EIGHTH = EIGHT;
    /**
     * 9
     */
    int NIGHT = 9;
    /**
     * 10
     */
    int TEN = 10;
    /**
     * 11
     */
    int ELEVEN = 11;
    /**
     * 12
     */
    int TWELVE = 12;
    /**
     * 23
     */
    int TWENTY_THREE = 23;
    /**
     * 24
     */
    int TWENTY_FOUR = 24;
    /**
     * 30
     */
    int THIRTY = 30;
    /**
     * 31
     */
    int THIRTY_ONE = 31;
    /**
     * 9
     */
    int NINTH = NIGHT;
    /**
     * 16
     */
    int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    /**
     * 32
     */
    int THIRTY_TWO = 4 * 8;
    /**
     * 0x01
     */
    int HEX_0X01 = 0x01;
    /**
     * bit -> 1
     */
    int BIT_LENGTH = EIGHT;
    /**
     * byte -> 1
     */
    int BYTE_LENGTH = ONE;
    /**
     * shore -> 2
     */
    int SHORE_LENGTH = TWE;
    /**
     * integer -> 4
     */
    int INTEGER_LENGTH = FOURTH;
    /**
     * long -> 8
     */
    int LONG_LENGTH = EIGHT;
    /**
     * 100
     */
    int ONE_HUNDRED = 100;
    /**
     * 1000
     */
    int ONE_THOUSAND = 1000;
    /**
     * 2000
     */
    int TWE_THOUSAND = 2000;
    /**
     * 10000
     */
    int TEN_THOUSAND = 10000;
    /**
     * 100000
     */
    int ONE_THOUSAND_THOUSAND = 10000;
    /**
     * 3000
     */
    int THREE_THOUSAND = 3000;
    /**
     * 64
     */
    int NUM_64 = 64;
    /**
     * 127
     */
    int NUM_127 = 127;
    /**
     * 128
     */
    int NUM_128 = 128;
    /**
     * 256
     */
    int NUM_256 = 256;
    /**
     * 512
     */
    int NUM_512 = 512;
    /**
     * 1024
     */
    int NUM_1024 = 1024;
    /**
     * 2048
     */
    int NUM_2048 = 2048;
    /**
     * 65535
     */
    int NUM_65535 = 65535;


    /**
     * 0x
     */
    String HEX_LOWER = "0x";
    /**
     * 0X
     */
    String HEX_UPPER = "0X";
    /**
     * 16
     */
    int HEX = 16;
    /**
     * 0.0f
     */
    float ZERO_FLOAT = 0.0f;
    /**
     * 0.0D
     */
    double ZERO_DOUBLE = 0.0D;
    /**
     * .9f
     */
    float ZERO_NIGHT_FLOAT = .9f;
    /**
     * 1.0f
     */
    float ONE_FLOAT = 1.f;
    /**
     * -1.0f
     */
    float NEG_ONE_FLOAT = -1.0f;
    /**
     * -1.0d
     */
    double NEG_ONE_DOUBLE = -1.0D;
    /**
     * 127
     */
    int MAX_127 = 127;
    /**
     * 128
     */
    int MAX_128 = 128;
    /**
     * 255
     */
    int MAX_255 = 255;
    /**
     * 256
     */
    int MAX_256 = 256;
    long K = 1024;
    int NIGHT_TEEN = 90;

    /**
     * BB
     */
    int BB = 0xBB;
    /**
     * BF
     */
    int BF = 0xBF;

    /**
     * FE
     */
    int FE = 0xFE;

    /**
     * EF
     */
    int EF = 0xEF;
    /**
     * FF
     */
    int FF = 0xFF;
    /**
     * 80
     */
    int X80 = 0x80;
    /**
     * 00
     */
    int X00 = 0x00;
    /**
     * 01
     */
    int X01 = 0x01;
    /**
     * 02
     */
    int X02 = 0x02;
    /**
     * 10
     */
    int X10 = 0x10;
    /**
     * 10
     */
    int XFFFF = 0xFFFF;
    /**
     * -1
     */
    int NUM_NEG_1 = -1;
    /**
     * 0
     */
    int NUM_0 = INTEGER_ZERO;
    /**
     * 1
     */
    int NUM_1 = ONE;
    /**
     * 2
     */
    int NUM_2 = TWE;
    /**
     * 3
     */
    int NUM_3 = THREE;
    /**
     * 4
     */
    int NUM_4 = FOUR;
    /**
     * 5
     */
    int NUM_5 = FIVE;
    /**
     * 6
     */
    int NUM_6 = SIX;
    /**
     * 7
     */
    int NUM_7 = SEVEN;
    /**
     * 8
     */
    int NUM_8 = EIGHT;
    /**
     * 9
     */
    int NUM_9 = NIGHT;
    /**
     * 10
     */
    int NUM_10 = TEN;
    /**
     * 11
     */
    int NUM_11 = 11;
    /**
     * 12
     */
    int NUM_12 = 12;
    /**
     * 13
     */
    int NUM_13 = 13;
    /**
     * 14
     */
    int NUM_14 = 14;
    /**
     * 16
     */
    int NUM_16 = 16;
    /**
     * 17
     */
    int NUM_17 = 17;
    /**
     * 18
     */
    int NUM_18 = 18;
    /**
     * 20
     */
    int NUM_20 = 20;
    /**
     * 24
     */
    int NUM_24 = 24;
    /**
     * 25
     */
    int NUM_25 = 25;
    /**
     * 26
     */
    int NUM_26 = 26;
    /**
     * 30
     */
    int NUM_30 = 30;
    /**
     * 31
     */
    int NUM_31 = 31;
    /**
     * 32
     */
    int NUM_32 = 32;
    /**
     * 34
     */
    int NUM_34 = 34;
    /**
     * 35
     */
    int NUM_35 = 45;
    /**
     * 43
     */
    int NUM_43 = 43;
    /**
     * 46
     */
    int NUM_46 = 46;
    /**
     * 63
     */
    int NUM_63 = 63;
    /**
     * 66
     */
    int NUM_66 = 66;
    /**
     * 77
     */
    int NUM_77 = 77;
    /**
     * 78
     */
    int NUM_78 = 78;
    /**
     * 88
     */
    int NUM_88 = 88;
    /**
     * 90
     */
    int NUM_90 = 90;
    /**
     * 99
     */
    int NUM_99 = 99;
    /**
     * 124
     */
    int NUM_124 = 124;
    /**
     * 125
     */
    int NUM_125 = 125;
    /**
     * 126
     */
    int NUM_126 = 126;
    /**
     * 155
     */
    int NUM_155 = 155;
    /**
     * 180
     */
    int NUM_180 = 180;
    /**
     * 220
     */
    int NUM_220 = 220;
    /**
     * 221
     */
    int NUM_221 = 221;
    /**
     * 255
     */
    int NUM_255 = MAX_255;
    /**
     * 360
     */
    int NUM_360 = 360;
    /**
     * 100
     */
    int NUM_100 = ONE_HUNDRED;
}
