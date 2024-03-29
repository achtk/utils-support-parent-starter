/*
 * BlockOutputStream
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

import com.chua.common.support.file.xz.check.BaseCheck;
import com.chua.common.support.file.xz.common.EncoderUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class BlockOutputStream extends AbstractFinishableOutputStream {
    private final OutputStream out;
    private final CountingOutputStream outCounted;
    private AbstractFinishableOutputStream filterChain;
    private final BaseCheck check;

    private final int headerSize;
    private final long compressedSizeLimit;
    private long uncompressedSize = 0;

    private final byte[] tempBuf = new byte[1];

    public BlockOutputStream(OutputStream out, FilterEncoder[] filters,
                             BaseCheck check, ArrayCache arrayCache)
            throws IOException {
        this.out = out;
        this.check = check;

        // Initialize the filter chain.
        outCounted = new CountingOutputStream(out);
        filterChain = outCounted;
        for (int i = filters.length - 1; i >= 0; --i)
            filterChain = filters[i].getOutputStream(filterChain, arrayCache);

        // Prepare to encode the Block Header field.
        ByteArrayOutputStream bufStream = new ByteArrayOutputStream();

        // Write a dummy Block Header Size field. The real value is written
        // once everything else except CRC32 has been written.
        bufStream.write(0x00);

        // Write Block Flags. Storing Compressed Size or Uncompressed Size
        // isn't supported for now.
        bufStream.write(filters.length - 1);

        // List of Filter Flags
        for (int i = 0; i < filters.length; ++i) {
            EncoderUtil.encodeVli(bufStream, filters[i].getFilterId());
            byte[] filterProps = filters[i].getFilterProps();
            EncoderUtil.encodeVli(bufStream, filterProps.length);
            bufStream.write(filterProps);
        }

        // Header Padding
        while ((bufStream.size() & 3) != 0)
            bufStream.write(0x00);

        byte[] buf = bufStream.toByteArray();

        // Total size of the Block Header: Take the size of the CRC32 field
        // into account.
        headerSize = buf.length + 4;

        // This is just a sanity check.
        if (headerSize > EncoderUtil.BLOCK_HEADER_SIZE_MAX)
            throw new UnsupportedOptionsException();

        // Block Header Size
        buf[0] = (byte)(buf.length / 4);

        // Write the Block Header field to the output stream.
        out.write(buf);
        EncoderUtil.writeCrc32(out, buf);

        // Calculate the maximum allowed size of the Compressed Data field.
        // It is hard to exceed it so this is mostly to be pedantic.
        compressedSizeLimit = (EncoderUtil.VLI_MAX & ~3)
                              - headerSize - check.getSize();
    }

    public void write(int b) throws IOException {
        tempBuf[0] = (byte)b;
        write(tempBuf, 0, 1);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        filterChain.write(buf, off, len);
        check.update(buf, off, len);
        uncompressedSize += len;
        validate();
    }

    public void flush() throws IOException {
        filterChain.flush();
        validate();
    }

    public void finish() throws IOException {
        // Finish the Compressed Data field.
        filterChain.finish();
        validate();

        // Block Padding
        for (long i = outCounted.getSize(); (i & 3) != 0; ++i)
            out.write(0x00);

        // Check
        out.write(check.finish());
    }

    private void validate() throws IOException {
        long compressedSize = outCounted.getSize();

        // It is very hard to trigger this exception.
        // This is just to be pedantic.
        if (compressedSize < 0 || compressedSize > compressedSizeLimit
                || uncompressedSize < 0)
            throw new XzException("XZ Stream has grown too big");
    }

    public long getUnpaddedSize() {
        return headerSize + outCounted.getSize() + check.getSize();
    }

    public long getUncompressedSize() {
        return uncompressedSize;
    }
}
