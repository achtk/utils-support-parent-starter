package com.chua.common.support.constant;

import java.io.File;
import java.math.BigInteger;

/**
 * 数字常量
 *
 * @author CH
 */
public class NumberConstant {
    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a kilobyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a megabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    /**
     * The number of bytes in a gigabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

    /**
     * The number of bytes in a terabyte.
     */
    public static final long ONE_TB = ONE_KB * ONE_GB;

    /**
     * The number of bytes in a terabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

    /**
     * The number of bytes in a petabyte.
     */
    public static final long ONE_PB = ONE_KB * ONE_TB;

    /**
     * The number of bytes in a petabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

    /**
     * The number of bytes in an exabyte.
     */
    public static final long ONE_EB = ONE_KB * ONE_PB;

    /**
     * The number of bytes in an exabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    /**
     * The number of bytes in a zettabyte.
     */
    public static final BigInteger ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));

    /**
     * The number of bytes in a yottabyte.
     */
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

    /**
     * An empty array of type {@code File}.
     */
    public static final File[] EMPTY_FILE_ARRAY = {};
    /**
     * 默认除法运算精度
     */
    public static final int DEFAUT_DIV_SCALE = 10;
    /**
     * Reusable Long constant for zero.
     */
    public static final Long LONG_ZERO = 0L;
    /**
     * Reusable Long constant for one.
     */
    public static final Long LONG_ONE = 1L;
    /**
     * Reusable Long constant for minus one.
     */
    public static final Long LONG_MINUS_ONE = -1L;
    /**
     * Reusable Integer constant for zero.
     */
    public static final Integer INTEGER_ZERO = 0;
    /**
     * Reusable Integer constant for one.
     */
    public static final Integer INTEGER_ONE = 1;
    /**
     * Reusable Integer constant for two
     */
    public static final Integer INTEGER_TWO = 2;
    /**
     * Reusable Integer constant for minus one.
     */
    public static final Integer INTEGER_MINUS_ONE = -1;
    /**
     * Reusable Short constant for zero.
     */
    public static final Short SHORT_ZERO = (short) 0;
    /**
     * Reusable Short constant for one.
     */
    public static final Short SHORT_ONE = (short) 1;
    /**
     * Reusable Short constant for minus one.
     */
    public static final Short SHORT_MINUS_ONE = (short) -1;
    /**
     * Reusable Byte constant for zero.
     */
    public static final Byte BYTE_ZERO = (byte) 0;
    /**
     * Reusable Byte constant for one.
     */
    public static final Byte BYTE_ONE = (byte) 1;
    /**
     * Reusable Byte constant for minus one.
     */
    public static final Byte BYTE_MINUS_ONE = (byte) -1;
    /**
     * Reusable Double constant for zero.
     */
    public static final Double DOUBLE_ZERO = 0.0d;
    /**
     * Reusable Double constant for one.
     */
    public static final Double DOUBLE_ONE = 1.0d;
    /**
     * Reusable Double constant for minus one.
     */
    public static final Double DOUBLE_MINUS_ONE = -1.0d;
    /**
     * Reusable Float constant for zero.
     */
    public static final Float FLOAT_ZERO = 0.0f;
    /**
     * Reusable Float constant for one.
     */
    public static final Float FLOAT_ONE = 1.0f;
    /**
     * Reusable Float constant for minus one.
     */
    public static final Float FLOAT_MINUS_ONE = -1.0f;

    public static final int STRING_BUILDER_SIZE = 256;

    public static final int INDEX_NOT_FOUND = -1;

    public static final int PAD_LIMIT = 8192;
    /**
     * 初始化大小
     */
    public static final int DEFAULT_SIZE = 1 << 4;

    /**
     * -1
     */
    public static final int EOF = INDEX_NOT_FOUND;

    public static final int SKIP_BUFFER_SIZE = 2048;
    /**
     * 1
     */
    public static final int ONE = 1;
    /**
     * 1
     */
    public static final int FIRST = ONE;
    /**
     * -2
     */
    public static final int NEGATIVE_TWE = -2;
    /**
     * 2
     */
    public static final int TWE = 2;
    /**
     * 2
     */
    public static final int SECOND = TWE;
    /**
     * 3
     */
    public static final int THREE = 3;
    /**
     * 3
     */
    public static final int THIRD = THREE;
    /**
     * 4
     */
    public static final int FOUR = 4;
    /**
     * 4
     */
    public static final int FOURTH = FOUR;
    /**
     * 5
     */
    public static final int FIVE = 5;
    /**
     * 5
     */
    public static final int FIFTH = FIVE;
    /**
     * 6
     */
    public static final int SIX = 6;
    /**
     * 6
     */
    public static final int SIXTH = SIX;
    /**
     * 7
     */
    public static final int SEVEN = 7;
    /**
     * 7
     */
    public static final int SEVENTH = SEVEN;
    /**
     * 8
     */
    public static final int EIGHT = 8;
    /**
     * 8
     */
    public static final int EIGHTH = EIGHT;
    /**
     * 9
     */
    public static final int NIGHT = 9;
    /**
     * 10
     */
    public static final int TEN = 10;
    /**
     * 11
     */
    public static final int ELEVEN = 11;
    /**
     * 12
     */
    public static final int TWELVE = 12;
    /**
     * 23
     */
    public static final int TWENTY_THREE = 23;
    /**
     * 24
     */
    public static final int TWENTY_FOUR = 24;
    /**
     * 30
     */
    public static final int THIRTY = 30;
    /**
     * 31
     */
    public static final int THIRTY_ONE = 31;
    /**
     * 9
     */
    public static final int NINTH = NIGHT;
    /**
     * 16
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    /**
     * 32
     */
    public static final int THIRTY_TWO = 4 * 8;
    /**
     * 0x01
     */
    public static final int HEX_0X01 = 0x01;
    /**
     * bit -> 1
     */
    public static final int BIT_LENGTH = EIGHT;
    /**
     * byte -> 1
     */
    public static final int BYTE_LENGTH = ONE;
    /**
     * shore -> 2
     */
    public static final int SHORE_LENGTH = TWE;
    /**
     * integer -> 4
     */
    public static final int INTEGER_LENGTH = FOURTH;
    /**
     * long -> 8
     */
    public static final int LONG_LENGTH = EIGHT;
    /**
     * 100
     */
    public static final int ONE_HUNDRED = 100;
    /**
     * 1000
     */
    public static final int ONE_THOUSAND = 1000;
    /**
     * 2000
     */
    public static final int TWE_THOUSAND = 2000;
    /**
     * 10000
     */
    public static final int TEN_THOUSAND = 10000;
    /**
     * 100000
     */
    public static final int ONE_THOUSAND_THOUSAND = 10000;
    /**
     * 3000
     */
    public static final int THREE_THOUSAND = 3000;
    /**
     * 64
     */
    public static final int NUM_64 = 64;
    /**
     * 127
     */
    public static final int NUM_127 = 127;
    /**
     * 128
     */
    public static final int NUM_128 = 128;
    /**
     * 256
     */
    public static final int NUM_256 = 256;
    /**
     * 512
     */
    public static final int NUM_512 = 512;
    /**
     * 1024
     */
    public static final int NUM_1024 = 1024;
    /**
     * 2048
     */
    public static final int NUM_2048 = 2048;
    /**
     * 65535
     */
    public static final int NUM_65535 = 65535;


    /**
     * 0x
     */
    public static final String HEX_LOWER = "0x";
    /**
     * 0X
     */
    public static final String HEX_UPPER = "0X";
    /**
     * 16
     */
    public static final int HEX = 16;
    /**
     * 0.0f
     */
    public static final float ZERO_FLOAT = 0.0f;
    /**
     * 0.0D
     */
    public static final double ZERO_DOUBLE = 0.0D;
    /**
     * .9f
     */
    public static final float ZERO_NIGHT_FLOAT = .9f;
    /**
     * 1.0f
     */
    public static final float ONE_FLOAT = 1.f;
    /**
     * -1.0f
     */
    public static final float NEG_ONE_FLOAT = -1.0f;
    /**
     * -1.0d
     */
    public static final double NEG_ONE_DOUBLE = -1.0D;
    /**
     * 127
     */
    public static final int MAX_127 = 127;
    /**
     * 128
     */
    public static final int MAX_128 = 128;
    /**
     * 255
     */
    public static final int MAX_255 = 255;
    /**
     * 256
     */
    public static final int MAX_256 = 256;
    public static final long K = 1024;
    public static final int NIGHT_TEEN = 90;

    /**
     * BB
     */
    public static final int BB = 0xBB;
    /**
     * BF
     */
    public static final int BF = 0xBF;

    /**
     * FE
     */
    public static final int FE = 0xFE;

    /**
     * EF
     */
    public static final int EF = 0xEF;
    /**
     * FF
     */
    public static final int FF = 0xFF;
    /**
     * 80
     */
    public static final int X80 = 0x80;
    /**
     * 00
     */
    public static final int X00 = 0x00;
    /**
     * 01
     */
    public static final int X01 = 0x01;
    /**
     * 02
     */
    public static final int X02 = 0x02;
    /**
     * 10
     */
    public static final int X10 = 0x10;
    /**
     * 10
     */
    public static final int XFFFF = 0xFFFF;
    /**
     * -1
     */
    public static final int NUM_NEG_1 = -1;
    /**
     * 0
     */
    public static final int NUM_0 = INTEGER_ZERO;
    /**
     * 1
     */
    public static final int NUM_1 = ONE;
    /**
     * 2
     */
    public static final int NUM_2 = TWE;
    /**
     * 3
     */
    public static final int NUM_3 = THREE;
    /**
     * 4
     */
    public static final int NUM_4 = FOUR;
    /**
     * 5
     */
    public static final int NUM_5 = FIVE;
    /**
     * 6
     */
    public static final int NUM_6 = SIX;
    /**
     * 7
     */
    public static final int NUM_7 = SEVEN;
    /**
     * 8
     */
    public static final int NUM_8 = EIGHT;
    /**
     * 9
     */
    public static final int NUM_9 = NIGHT;
    /**
     * 10
     */
    public static final int NUM_10 = TEN;
    /**
     * 11
     */
    public static final int NUM_11 = 11;
    /**
     * 12
     */
    public static final int NUM_12 = 12;
    /**
     * 13
     */
    public static final int NUM_13 = 13;
    /**
     * 14
     */
    public static final int NUM_14 = 14;
    /**
     * 16
     */
    public static final int NUM_16 = 16;
    /**
     * 17
     */
    public static final int NUM_17 = 17;
    /**
     * 18
     */
    public static final int NUM_18 = 18;
    /**
     * 20
     */
    public static final int NUM_20 = 20;
    /**
     * 24
     */
    public static final int NUM_24 = 24;
    /**
     * 25
     */
    public static final int NUM_25 = 25;
    /**
     * 26
     */
    public static final int NUM_26 = 26;
    /**
     * 30
     */
    public static final int NUM_30 = 30;
    /**
     * 31
     */
    public static final int NUM_31 = 31;
    /**
     * 32
     */
    public static final int NUM_32 = 32;
    /**
     * 34
     */
    public static final int NUM_34 = 34;
    /**
     * 35
     */
    public static final int NUM_35 = 45;
    /**
     * 43
     */
    public static final int NUM_43 = 43;
    /**
     * 46
     */
    public static final int NUM_46 = 46;
    /**
     * 63
     */
    public static final int NUM_63 = 63;
    /**
     * 66
     */
    public static final int NUM_66 = 66;
    /**
     * 77
     */
    public static final int NUM_77 = 77;
    /**
     * 78
     */
    public static final int NUM_78 = 78;
    /**
     * 88
     */
    public static final int NUM_88 = 88;
    /**
     * 90
     */
    public static final int NUM_90 = 90;
    /**
     * 99
     */
    public static final int NUM_99 = 99;
    /**
     * 124
     */
    public static final int NUM_124 = 124;
    /**
     * 125
     */
    public static final int NUM_125 = 125;
    /**
     * 126
     */
    public static final int NUM_126 = 126;
    /**
     * 155
     */
    public static final int NUM_155 = 155;
    /**
     * 180
     */
    public static final int NUM_180 = 180;
    /**
     * 220
     */
    public static final int NUM_220 = 220;
    /**
     * 221
     */
    public static final int NUM_221 = 221;
    /**
     * 255
     */
    public static final int NUM_255 = MAX_255;
    /**
     * 360
     */
    public static final int NUM_360 = 360;
    /**
     * 100
     */
    public static final int NUM_100 = ONE_HUNDRED;
}
