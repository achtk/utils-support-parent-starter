package com.chua.common.support.binary;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

/**
 * byte[]
 *
 * @author CH
 */
public class BinaryOutputStream extends OutputStream {
    private final OutputStream os;
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    private int count;

    public BinaryOutputStream(final OutputStream os, final ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        this.os = os;
    }

    public BinaryOutputStream(final OutputStream os) {
        this.os = os;
    }

    protected void setByteOrder(final ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    @Override
    public void write(final int i) throws IOException {
        os.write(i);
        count++;
    }

    @Override
    public final void write(final byte[] bytes) throws IOException {
        os.write(bytes, 0, bytes.length);
        count += bytes.length;
    }

    @Override
    public final void write(final byte[] bytes, final int offset, final int length) throws IOException {
        os.write(bytes, offset, length);
        count += length;
    }

    @Override
    public void flush() throws IOException {
        os.flush();
    }

    @Override
    public void close() throws IOException {
        os.close();
    }

    public int getByteCount() {
        return count;
    }

    public final void write4Bytes(final int value) throws IOException {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            write(0xff & (value >> 24));
            write(0xff & (value >> 16));
            write(0xff & (value >> 8));
            write(0xff & value);
        } else {
            write(0xff & value);
            write(0xff & (value >> 8));
            write(0xff & (value >> 16));
            write(0xff & (value >> 24));
        }
    }

    public final void write3Bytes(final int value) throws IOException {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            write(0xff & (value >> 16));
            write(0xff & (value >> 8));
            write(0xff & value);
        } else {
            write(0xff & value);
            write(0xff & (value >> 8));
            write(0xff & (value >> 16));
        }
    }

    public final void write2Bytes(final int value) throws IOException {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            write(0xff & (value >> 8));
            write(0xff & value);
        } else {
            write(0xff & value);
            write(0xff & (value >> 8));
        }
    }
}
