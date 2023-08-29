package com.chua.common.support.file.xz.index;

import com.chua.common.support.file.xz.CorruptedInputException;
import com.chua.common.support.file.xz.XzException;
import com.chua.common.support.file.xz.check.Check;
import com.chua.common.support.file.xz.check.Crc32;
import com.chua.common.support.file.xz.check.Sha256;
import com.chua.common.support.file.xz.common.DecoderUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CheckedInputStream;

public class IndexHash extends IndexBase {
    private Check hash;

    public IndexHash() {
        super(new CorruptedInputException());

        try {
            hash = new Sha256();
        } catch (java.security.NoSuchAlgorithmException e) {
            hash = new Crc32();
        }
    }

    public void add(long unpaddedSize, long uncompressedSize)
            throws XzException {
        super.add(unpaddedSize, uncompressedSize);

        ByteBuffer buf = ByteBuffer.allocate(2 * 8);
        buf.putLong(unpaddedSize);
        buf.putLong(uncompressedSize);
        hash.update(buf.array());
    }

    public void validate(InputStream in) throws IOException {
        // Index Indicator (0x00) has already been read by BlockInputStream
        // so add 0x00 to the CRC32 here.
        java.util.zip.CRC32 crc32 = new java.util.zip.CRC32();
        crc32.update('\0');
        CheckedInputStream inChecked = new CheckedInputStream(in, crc32);

        // Get and validate the Number of Records field.
        // If Block Header Size was corrupt and became Index Indicator,
        // this error would actually be about corrupt Block Header.
        // This is why the error message mentions both possibilities.
        long storedRecordCount = DecoderUtil.decodeVli(inChecked);
        if (storedRecordCount != recordCount)
            throw new CorruptedInputException(
                    "XZ Block Header or the start of XZ Index is corrupt");

        // Decode and hash the Index field and compare it to
        // the hash value calculated from the decoded Blocks.
        IndexHash stored = new IndexHash();
        for (long i = 0; i < recordCount; ++i) {
            long unpaddedSize = DecoderUtil.decodeVli(inChecked);
            long uncompressedSize = DecoderUtil.decodeVli(inChecked);

            try {
                stored.add(unpaddedSize, uncompressedSize);
            } catch (XzException e) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }

            if (stored.blocksSum > blocksSum
                    || stored.uncompressedSum > uncompressedSum
                    || stored.indexListSize > indexListSize)
                throw new CorruptedInputException("XZ Index is corrupt");
        }

        if (stored.blocksSum != blocksSum
                || stored.uncompressedSum != uncompressedSum
                || stored.indexListSize != indexListSize
                || !Arrays.equals(stored.hash.finish(), hash.finish()))
            throw new CorruptedInputException("XZ Index is corrupt");

        // Index Padding
        DataInputStream inData = new DataInputStream(inChecked);
        for (int i = getIndexPaddingSize(); i > 0; --i)
            if (inData.readUnsignedByte() != 0x00)
                throw new CorruptedInputException("XZ Index is corrupt");

        // CRC32
        long value = crc32.getValue();
        for (int i = 0; i < 4; ++i)
            if (((value >>> (i * 8)) & 0xFF) != inData.readUnsignedByte())
                throw new CorruptedInputException("XZ Index is corrupt");
    }
}
