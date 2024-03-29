/*
 * EncoderUtil
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

import static com.chua.common.support.constant.NumberConstant.FOUR;

/**
 * @author Administrator
 */
public class EncoderUtil extends Util {
    public static void writeCrc32(OutputStream out, byte[] buf)
            throws IOException {
        CRC32 crc32 = new CRC32();
        crc32.update(buf);
        long value = crc32.getValue();

        for (int i = 0; i < FOUR; ++i) {
            out.write((byte) (value >>> (i * 8)));
        }
    }

    public static void encodeVli(OutputStream out, long num)
            throws IOException {
        long x80 = 0x80;
        while (num >= x80) {
            out.write((byte) (num | 0x80));
            num >>>= 7;
        }

        out.write((byte) num);
    }
}
