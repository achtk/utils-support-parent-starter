/*
 * CRC32Hash
 *
 * Authors: Lasse Collin <lasse.collin@tukaani.org>
 *          Igor Pavlov <http://7-zip.org/>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.lz;

import static com.chua.common.support.constant.NumberConstant.EIGHT;
import static com.chua.common.support.constant.NumberConstant.NUM_256;

/**
 * Provides a CRC32 table using the polynomial from IEEE 802.3.
 *
 * @author Administrator
 */
class Crc32Hash {
    private static final int CRC32_POLY = 0xEDB88320;

    static final int[] CRC_TABLE = new int[256];

    static {
        for (int i = 0; i < NUM_256; ++i) {
            int r = i;

            for (int j = 0; j < EIGHT; ++j) {
                if ((r & 1) != 0) {
                    r = (r >>> 1) ^ CRC32_POLY;
                } else {
                    r >>>= 1;
                }
            }

            CRC_TABLE[i] = r;
        }
    }
}
