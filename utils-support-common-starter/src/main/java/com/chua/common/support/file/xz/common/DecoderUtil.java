/*
 * DecoderUtil
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.common;

import com.chua.common.support.file.xz.CorruptedInputException;
import com.chua.common.support.file.xz.UnsupportedOptionsException;
import com.chua.common.support.file.xz.XZFormatException;
import com.chua.common.support.file.xz.Xz;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 * @author Administrator
 */
public class DecoderUtil extends Util {
    public static boolean isCrc32Valid(byte[] buf, int off, int len,
                                       int refOff) {
        CRC32 crc32 = new CRC32();
        crc32.update(buf, off, len);
        long value = crc32.getValue();

        for (int i = 0; i < 4; ++i) {
            if ((byte) (value >>> (i * 8)) != buf[refOff + i]) {
                return false;
            }
        }

        return true;
    }

    public static StreamFlags decodeStreamHeader(byte[] buf)
            throws IOException {
        for (int i = 0; i < Xz.HEADER_MAGIC.length; ++i) {
            if (buf[i] != Xz.HEADER_MAGIC[i]) {
                throw new XZFormatException();
            }
        }

        if (!isCrc32Valid(buf, Xz.HEADER_MAGIC.length, 2,
                Xz.HEADER_MAGIC.length + 2)) {
            throw new CorruptedInputException("XZ Stream Header is corrupt");
        }

        try {
            return decodeStreamFlags(buf, Xz.HEADER_MAGIC.length);
        } catch (UnsupportedOptionsException e) {
            throw new UnsupportedOptionsException(
                    "Unsupported options in XZ Stream Header");
        }
    }

    public static StreamFlags decodeStreamFooter(byte[] buf)
            throws IOException {
        if (buf[10] != Xz.FOOTER_MAGIC[0] || buf[11] != Xz.FOOTER_MAGIC[1]) {
            // NOTE: The exception could be XZFormatException too.
            // It depends on the situation which one is better.
            throw new CorruptedInputException("XZ Stream Footer is corrupt");
        }

        if (!isCrc32Valid(buf, 4, 6, 0)) {
            throw new CorruptedInputException("XZ Stream Footer is corrupt");
        }

        StreamFlags streamFlags;
        try {
            streamFlags = decodeStreamFlags(buf, 8);
        } catch (UnsupportedOptionsException e) {
            throw new UnsupportedOptionsException(
                    "Unsupported options in XZ Stream Footer");
        }

        streamFlags.backwardSize = 0;
        for (int i = 0; i < 4; ++i) {
            streamFlags.backwardSize |= (buf[i + 4] & 0xFF) << (i * 8);
        }

        streamFlags.backwardSize = (streamFlags.backwardSize + 1) * 4;

        return streamFlags;
    }

    private static StreamFlags decodeStreamFlags(byte[] buf, int off)
            throws UnsupportedOptionsException {
        if (buf[off] != 0x00 || (buf[off + 1] & 0xFF) >= 0x10) {
            throw new UnsupportedOptionsException();
        }

        StreamFlags streamFlags = new StreamFlags();
        streamFlags.checkType = buf[off + 1];

        return streamFlags;
    }

    public static boolean areStreamFlagsEqual(StreamFlags a, StreamFlags b) {
        // backwardSize is intentionally not compared.
        return a.checkType == b.checkType;
    }

    public static long decodeVli(InputStream in) throws IOException {
        int b = in.read();
        if (b == -1) {
            throw new EOFException();
        }

        long num = b & 0x7F;
        int i = 0;

        while ((b & 0x80) != 0x00) {
            if (++i >= VLI_SIZE_MAX) {
                throw new CorruptedInputException();
            }

            b = in.read();
            if (b == -1) {
                throw new EOFException();
            }

            if (b == 0x00) {
                throw new CorruptedInputException();
            }

            num |= (long)(b & 0x7F) << (i * 7);
        }

        return num;
    }
}
