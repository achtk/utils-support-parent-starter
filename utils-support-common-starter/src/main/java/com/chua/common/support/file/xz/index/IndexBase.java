/*
 * IndexBase
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.index;

import com.chua.common.support.file.xz.XzException;
import com.chua.common.support.file.xz.common.Util;

abstract class IndexBase {
    private final XzException invalidIndexException;
    long blocksSum = 0;
    long uncompressedSum = 0;
    long indexListSize = 0;
    long recordCount = 0;

    IndexBase(XzException invalidIndexException) {
        this.invalidIndexException = invalidIndexException;
    }

    private long getUnpaddedIndexSize() {
        // Index Indicator + Number of Records + List of Records + CRC32
        return 1 + Util.getVliSize(recordCount) + indexListSize + 4;
    }

    public long getIndexSize() {
        return (getUnpaddedIndexSize() + 3) & ~3;
    }

    public long getStreamSize() {
        return Util.STREAM_HEADER_SIZE + blocksSum + getIndexSize()
                + Util.STREAM_HEADER_SIZE;
    }

    int getIndexPaddingSize() {
        return (int)((4 - getUnpaddedIndexSize()) & 3);
    }

    void add(long unpaddedSize, long uncompressedSize) throws XzException {
        blocksSum += (unpaddedSize + 3) & ~3;
        uncompressedSum += uncompressedSize;
        indexListSize += Util.getVliSize(unpaddedSize)
                         + Util.getVliSize(uncompressedSize);
        ++recordCount;

        if (blocksSum < 0 || uncompressedSum < 0
                || getIndexSize() > Util.BACKWARD_SIZE_MAX
                || getStreamSize() < 0) {
            throw invalidIndexException;
        }
    }
}
