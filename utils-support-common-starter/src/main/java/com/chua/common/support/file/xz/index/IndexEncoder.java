/*
 * IndexEncoder
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.index;

import com.chua.common.support.file.xz.XzException;
import com.chua.common.support.file.xz.common.EncoderUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.CheckedOutputStream;

public class IndexEncoder extends BaseIndexBase {
    private final ArrayList<IndexRecord> records
            = new ArrayList<IndexRecord>();

    public IndexEncoder() {
        super(new XzException("XZ Stream or its Index has grown too big"));
    }

    public void add(long unpaddedSize, long uncompressedSize)
            throws XzException {
        super.add(unpaddedSize, uncompressedSize);
        records.add(new IndexRecord(unpaddedSize, uncompressedSize));
    }

    public void encode(OutputStream out) throws IOException {
        java.util.zip.CRC32 crc32 = new java.util.zip.CRC32();
        CheckedOutputStream outChecked = new CheckedOutputStream(out, crc32);

        // Index Indicator
        outChecked.write(0x00);

        // Number of Records
        EncoderUtil.encodeVli(outChecked, recordCount);

        // List of Records
        for (IndexRecord record : records) {
            EncoderUtil.encodeVli(outChecked, record.unpadded);
            EncoderUtil.encodeVli(outChecked, record.uncompressed);
        }

        // Index Padding
        for (int i = getIndexPaddingSize(); i > 0; --i)
            outChecked.write(0x00);

        // CRC32
        long value = crc32.getValue();
        for (int i = 0; i < 4; ++i)
            out.write((byte)(value >>> (i * 8)));
    }
}
