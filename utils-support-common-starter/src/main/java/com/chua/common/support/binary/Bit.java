package com.chua.common.support.binary;

import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.BitUtils;

/**
 * 比特
 *
 * @author CH
 */
public class Bit {
    private final byte[] bytes;

    private Bit(byte[] bytes) {

        this.bytes = bytes;
    }

    public static Bit of(byte[] bytes) {
        return new Bit(bytes);
    }

    /**
     * sub
     *
     * @param start 开始
     * @param end   终止
     * @return {@link Bit}
     */
    public Bit sub(int start, int end) {
        byte[] asBit = BitUtils.asBit(bytes);
        ArrayUtils.reverse(asBit);
        byte[] bytes = ArrayUtils.subArray(asBit, start, end);
        ArrayUtils.reverse(bytes);
        return Bit.of(bytes);
    }

    /**
     * 作为位
     * 作为字节
     *
     * @param length 长
     * @return byte
     */
    private byte asBit(int length) {
        byte[] tplByteArray = new byte[length];
        System.arraycopy(bytes, 0, tplByteArray, tplByteArray.length - bytes.length, bytes.length );
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : tplByteArray) {
            stringBuilder.append(b);
        }
        return Byte.parseByte(stringBuilder.toString(), 2);
    }
    /**
     * 作为字节
     *
     * @return byte
     */
    public byte asByte() {
        return asBit(8);
    }
    /**
     * 作为字节
     *
     * @return byte
     */
    public byte asShort() {
        return asBit(16);
    }
    /**
     * 作为字节
     *
     * @return byte
     */
    public byte asInt() {
        return asBit(32);
    }
}
