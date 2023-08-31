/*
 * CRC64
 *
 * Authors: Brett Okken <brett.okken.os@gmail.com>
 *          Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.check;

import static com.chua.common.support.constant.NumberConstant.*;

/**
 * @author Administrator
 */
public class Crc64 extends BaseCheck {
    private static final long[][] TABLE = new long[4][256];

    static {
        final long poly64 = 0xC96C5795D7870F42L;

        for (int s = 0; s < NUM_4; ++s) {
            for (int b = 0; b < NUM_256; ++b) {
                long r = s == 0 ? b : TABLE[s - 1][b];
                for (int i = 0; i < NUM_8; ++i) {
                    if ((r & 1) == 1) {
                        r = (r >>> 1) ^ poly64;
                    } else {
                        r >>>= 1;
                    }
                }
                TABLE[s][b] = r;
            }
        }
    }

    private long crc = -1;

    public Crc64() {
        size = 8;
        name = "CRC64";
    }

    @Override
    public void update(byte[] buf, int off, int len) {
        final int end = off + len;
        int i = off;

        for (int end4 = end - NUM_3; i < end4; i += NUM_4) {
            final int tmp = (int) crc;
            crc = TABLE[3][(tmp & 0xFF) ^ (buf[i] & 0xFF)] ^
                    TABLE[2][((tmp >>> 8) & 0xFF) ^ (buf[i + 1] & 0xFF)] ^
                    (crc >>> 32) ^
                    TABLE[1][((tmp >>> 16) & 0xFF) ^ (buf[i + 2] & 0xFF)] ^
                    TABLE[0][((tmp >>> 24) & 0xFF) ^ (buf[i + 3] & 0xFF)];
        }

        while (i < end) {
            crc = TABLE[0][(buf[i++] & 0xFF) ^ ((int) crc & 0xFF)] ^
                    (crc >>> 8);
        }
    }

    @Override
    public byte[] finish() {
        long value = ~crc;
        crc = -1;

        byte[] buf = new byte[8];
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = (byte) (value >> (i * 8));
        }

        return buf;
    }
}
