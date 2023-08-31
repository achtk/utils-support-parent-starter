package com.chua.common.support.file.xz.check;

public class Crc32 extends BaseCheck {
    private final java.util.zip.CRC32 state = new java.util.zip.CRC32();

    public Crc32() {
        size = 4;
        name = "CRC32";
    }

    public void update(byte[] buf, int off, int len) {
        state.update(buf, off, len);
    }

    public byte[] finish() {
        long value = state.getValue();
        byte[] buf = { (byte)(value),
                       (byte)(value >>> 8),
                       (byte)(value >>> 16),
                       (byte)(value >>> 24) };
        state.reset();
        return buf;
    }
}
