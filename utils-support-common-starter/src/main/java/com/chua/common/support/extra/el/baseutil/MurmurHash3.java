package com.chua.common.support.extra.el.baseutil;

/**
 * The MurmurHash3 algorithm was created by Austin Appleby and placed in the
 * public domain. This java port was authored by Yonik Seeley and also placed
 * into the public domain. The author hereby disclaims copyright to this source
 * code.
 * <p>
 * This produces exactly the same hash values as the final C++ version of
 * MurmurHash3 and is thus suitable for producing the same hash values across
 * platforms.
 * <p>
 * The 32 bit x86 version of this hash should be the fastest variant for
 * relatively short keys like ids. is a good choice for
 * longer strings or if you need more than 32 bits of hash.
 * <p>
 * Note - The x86 and x64 versions do _not_ produce the same results, as the
 * algorithms are optimized for their respective platforms.
 * <p>
 * See http:
 * @author Administrator
 */
public final class MurmurHash3 {
    public static final int SEED = 0x1234ABCD;

    public static final int fmix32(int h) {
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return h;
    }

    public static final long fmix64(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;
        return k;
    }

    /**
     * Gets a long from a byte buffer in little endian byte order.
     */
    public static final long getLongLittleEndian(byte[] buf, int offset) {
        return ((long) buf[offset + 7] << 56) 
                | ((buf[offset + 6] & 0xffL) << 48) | ((buf[offset + 5] & 0xffL) << 40) | ((buf[offset + 4] & 0xffL) << 32) | ((buf[offset + 3] & 0xffL) << 24) | ((buf[offset + 2] & 0xffL) << 16) | ((buf[offset + 1] & 0xffL) << 8) | ((buf[offset] & 0xffL)); 
        
        
    }

    /**
     * 128 bits of state
     */
    public static final class LongPair {
        public long val1;
        public long val2;
    }
}
