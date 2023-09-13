package com.chua.proxy.support.utils;

import io.netty.buffer.ByteBuf;

/**
 * 有用buf
 *
 * @author CH
 * @since 2023/09/13
 */
public final class BufUtils {

    /**
     * 获取数组
     *
     * @param buf buf
     * @return {@link byte[]}
     */
    public static byte[] getArray(ByteBuf buf) {
        byte[] result = null;
        if (buf.hasArray()) {
            result = buf.array();
        } else {
            result = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), result);
        }
        return result;
    }

    /**
     * xor
     *
     * @param buf buf
     * @return byte
     */
    public static byte xor(ByteBuf buf) {
        byte result = 0x00;
        int len = buf.readableBytes();
        for (int i = 0; i < len; i++) {
            result ^= buf.getByte(i);
        }
        return result;
    }

    /**
     * xor
     *
     * @param buf   buf
     * @param begin 开始
     * @param end   终止
     * @return byte
     */
    public static byte xor(ByteBuf buf, int begin, int end) {
        byte result = 0x00;
        for (int i = begin; i < end; i++) {
            result ^= buf.getByte(i);
        }
        return result;
    }

    /**
     * 总和
     *
     * @param buf buf
     * @return byte
     */
    public static byte sum(ByteBuf buf) {
        byte result = 0x00;
        int len = buf.readableBytes();
        for (int i = 0; i < len; i++) {
            result += buf.getByte(i);
        }
        return result;
    }

    /**
     * 总和
     *
     * @param buf   buf
     * @param begin 开始
     * @param end   终止
     * @return byte
     */
    public static byte sum(ByteBuf buf, int begin, int end) {
        byte result = 0x00;
        for (int i = begin; i < end; i++) {
            result += buf.getByte(i);
        }
        return result;
    }

}
