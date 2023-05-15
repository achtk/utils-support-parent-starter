package com.chua.common.support.binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ByteSourceArray
 *
 * @author CH
 * @since 2022-01-14
 */
public class AbstractByteSourceArray extends AbstractByteSource {
    private final byte[] bytes;

    public AbstractByteSourceArray(final String filename, final byte[] bytes) {
        super(filename);
        this.bytes = bytes;
    }

    public AbstractByteSourceArray(final byte[] bytes) {
        this(null, bytes);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public byte[] getBlock(final long startLong, final int length) throws IOException {
        final int start = (int) startLong;
        // We include a separate check for int overflow.
        if ((start < 0) || (length < 0) || (start + length < 0)
                || (start + length > bytes.length)) {
            throw new IOException("Could not read block (block start: " + start
                    + ", block length: " + length + ", data length: "
                    + bytes.length + ").");
        }

        final byte[] result = new byte[length];
        System.arraycopy(bytes, start, result, 0, length);
        return result;
    }

    @Override
    public long getLength() {
        return bytes.length;
    }

    @Override
    public byte[] getAll() throws IOException {
        return bytes;
    }

    @Override
    public String getDescription() {
        return bytes.length + " byte array";
    }

}