package com.chua.common.support.binary;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 二进制常理
 *
 * @author CH
 * @since 2022-01-14
 */
public class BinaryConstant {
    private final byte[] value;

    public BinaryConstant(final byte[] value) {
        this.value = value.clone();
    }

    @Override
    public BinaryConstant clone() throws CloneNotSupportedException {
        return (BinaryConstant) super.clone();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BinaryConstant)) {
            return false;
        }
        final BinaryConstant other = (BinaryConstant) obj;
        return equals(other.value);
    }

    public boolean equals(final byte[] bytes) {
        return Arrays.equals(value, bytes);
    }

    public boolean equals(final byte[] bytes, final int offset, final int length) {
        if (value.length != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (value[i] != bytes[offset + i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    public byte get(final int i) {
        return value[i];
    }

    public int size() {
        return value.length;
    }

    public byte[] toByteArray() {
        return value.clone();
    }

    public void writeTo(final OutputStream os) throws IOException {
        for (final byte element : value) {
            os.write(element);
        }
    }
}
