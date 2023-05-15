/*
 * IndexDecoder
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.index;

import com.chua.common.support.file.xz.CorruptedInputException;
import com.chua.common.support.file.xz.MemoryLimitException;
import com.chua.common.support.file.xz.SeekableInputStream;
import com.chua.common.support.file.xz.UnsupportedOptionsException;
import com.chua.common.support.file.xz.common.DecoderUtil;
import com.chua.common.support.file.xz.common.StreamFlags;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.CheckedInputStream;

/**
 * @author Administrator
 */
public class IndexDecoder extends IndexBase {
    private final StreamFlags streamFlags;
    private final long streamPadding;
    private final int memoryUsage;

    private final long[] unpadded;
    private final long[] uncompressed;

    private long largestBlockSize = 0;

    private int recordOffset = 0;
    private long compressedOffset = 0;
    private long uncompressedOffset = 0;

    public IndexDecoder(SeekableInputStream in, StreamFlags streamFooterFlags,
                        long streamPadding, int memoryLimit)
            throws IOException {
        super(new CorruptedInputException("XZ Index is corrupt"));
        this.streamFlags = streamFooterFlags;
        this.streamPadding = streamPadding;

        long endPos = in.position() + streamFooterFlags.backwardSize - 4;

        java.util.zip.CRC32 crc32 = new java.util.zip.CRC32();
        CheckedInputStream inChecked = new CheckedInputStream(in, crc32);

        if (inChecked.read() != 0x00) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }

        try {
            long count = DecoderUtil.decodeVli(inChecked);

            if (count >= streamFooterFlags.backwardSize / 2) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }

            if (count > Integer.MAX_VALUE) {
                throw new UnsupportedOptionsException("XZ Index has over "
                        + Integer.MAX_VALUE + " Records");
            }

            memoryUsage = 1 + (int) ((16L * count + 1023) / 1024);
            if (memoryLimit >= 0 && memoryUsage > memoryLimit) {
                throw new MemoryLimitException(memoryUsage, memoryLimit);
            }

            unpadded = new long[(int) count];
            uncompressed = new long[(int) count];
            int record = 0;

            for (int i = (int) count; i > 0; --i) {
                long unpaddedSize = DecoderUtil.decodeVli(inChecked);
                long uncompressedSize = DecoderUtil.decodeVli(inChecked);

                if (in.position() > endPos) {
                    throw new CorruptedInputException("XZ Index is corrupt");
                }

                unpadded[record] = blocksSum + unpaddedSize;
                uncompressed[record] = uncompressedSum + uncompressedSize;
                ++record;
                super.add(unpaddedSize, uncompressedSize);
                assert record == recordCount;

                if (largestBlockSize < uncompressedSize) {
                    largestBlockSize = uncompressedSize;
                }
            }
        } catch (EOFException e) {
            // DecoderUtil.decodeVLI to read too much at once.
            throw new CorruptedInputException("XZ Index is corrupt");
        }

        int indexPaddingSize = getIndexPaddingSize();
        if (in.position() + indexPaddingSize != endPos) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }

        while (indexPaddingSize-- > 0) {
            if (inChecked.read() != 0x00) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }

        long value = crc32.getValue();
        for (int i = 0; i < 4; ++i) {
            if (((value >>> (i * 8)) & 0xFF) != in.read()) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
    }

    public void setOffsets(IndexDecoder prev) {
        recordOffset = prev.recordOffset + (int) prev.recordCount;
        compressedOffset = prev.compressedOffset
                + prev.getStreamSize() + prev.streamPadding;
        assert (compressedOffset & 3) == 0;
        uncompressedOffset = prev.uncompressedOffset + prev.uncompressedSum;
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }

    public StreamFlags getStreamFlags() {
        return streamFlags;
    }

    public int getRecordCount() {
        return (int) recordCount;
    }

    public long getUncompressedSize() {
        return uncompressedSum;
    }

    public long getLargestBlockSize() {
        return largestBlockSize;
    }

    public boolean hasUncompressedOffset(long pos) {
        return pos >= uncompressedOffset
                && pos < uncompressedOffset + uncompressedSum;
    }

    public boolean hasRecord(int blockNumber) {
        return blockNumber >= recordOffset
                && blockNumber < recordOffset + recordCount;
    }

    public void locateBlock(BlockInfo info, long target) {
        assert target >= uncompressedOffset;
        target -= uncompressedOffset;
        assert target < uncompressedSum;

        int left = 0;
        int right = unpadded.length - 1;

        while (left < right) {
            int i = left + (right - left) / 2;

            if (uncompressed[i] <= target) {
                left = i + 1;
            } else {
                right = i;
            }
        }

        setBlockInfo(info, recordOffset + left);
    }

    public void setBlockInfo(BlockInfo info, int blockNumber) {
        assert blockNumber >= recordOffset;
        assert blockNumber - recordOffset < recordCount;

        info.index = this;
        info.blockNumber = blockNumber;

        int pos = blockNumber - recordOffset;

        if (pos == 0) {
            info.compressedOffset = 0;
            info.uncompressedOffset = 0;
        } else {
            info.compressedOffset = (unpadded[pos - 1] + 3) & ~3;
            info.uncompressedOffset = uncompressed[pos - 1];
        }

        info.unpaddedSize = unpadded[pos] - info.compressedOffset;
        info.uncompressedSize = uncompressed[pos] - info.uncompressedOffset;

        info.compressedOffset += compressedOffset
                + DecoderUtil.STREAM_HEADER_SIZE;
        info.uncompressedOffset += uncompressedOffset;
    }
}
