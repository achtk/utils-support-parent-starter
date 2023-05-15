package com.chua.common.support.binary;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author CH
 * @since 2022-01-14
 */
public class FastByteArrayOutputStream extends OutputStream {
    private final byte[] bytes;
    private int count;

    public FastByteArrayOutputStream(final int length) {
        bytes = new byte[length];
    }

    @Override
    public void write(final int value) throws IOException {
        if (count >= bytes.length) {
            throw new IOException("Write exceeded expected length (" + count + ", " + bytes.length + ")");
        }

        bytes[count] = (byte) value;
        count++;
    }

    public byte[] toByteArray() {
        if (count < bytes.length) {
            final byte[] result = new byte[count];
            System.arraycopy(bytes, 0, result, 0, count);
            return result;
        }
        return bytes;
    }

    public int getBytesWritten() {
        return count;
    }
}
