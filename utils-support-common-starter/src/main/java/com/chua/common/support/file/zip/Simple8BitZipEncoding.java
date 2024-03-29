package com.chua.common.support.file.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This ZipEncoding implementation implements a simple 8 bit character
 * set, which meets the following restrictions:
 *
 * <ul>
 * <li>Characters 0x0000 to 0x007f are encoded as the corresponding
 *        byte values 0x00 to 0x7f.</li>
 * <li>All byte codes from 0x80 to 0xff are mapped to a unique Unicode
 *       character in the range 0x0080 to 0x7fff. (No support for
 *       UTF-16 surrogates)
 * </ul>
 *
 * <p>These restrictions most notably apply to the most prominent
 * omissions of Java 1.4 {@link java.nio.charset.Charset Charset}
 * implementation, Cp437 and Cp850.</p>
 *
 * <p>The methods of this class are reentrant.</p>
 */
class Simple8BitZipEncoding implements ZipEncoding {

    /**
     * The characters for byte values of 128 to 255 stored as an array of
     * 128 chars.
     */
    private final char[] highChars;
    /**
     * A list of {@link Simple8BitChar} objects sorted by the unicode
     * field.  This list is used to binary search reverse mapping of
     * unicode characters with a character code greater than 127.
     */
    private final List<Simple8BitChar> reverseMapping;

    /**
     * @param highChars The characters for byte values of 128 to 255
     *                  stored as an array of 128 chars.
     */
    public Simple8BitZipEncoding(final char[] highChars) {
        this.highChars = highChars.clone();
        final List<Simple8BitChar> temp =
                new ArrayList<>(this.highChars.length);

        byte code = 127;

        for (char highChar : this.highChars) {
            temp.add(new Simple8BitChar(++code, highChar));
        }

        Collections.sort(temp);
        this.reverseMapping = Collections.unmodifiableList(temp);
    }

    /**
     * Return the character code for a given encoded byte.
     *
     * @param b The byte to decode.
     * @return The associated character value.
     */
    public char decodeByte(final byte b) {
        // code 0-127
        if (b >= 0) {
            return (char) b;
        }

        // byte is signed, so 128 == -128 and 255 == -1
        return this.highChars[128 + b];
    }

    /**
     * @param c The character to encode.
     * @return Whether the given unicode character is covered by this encoding.
     */
    public boolean canEncodeChar(final char c) {

        if (c >= 0 && c < 128) {
            return true;
        }

        final Simple8BitChar r = this.encodeHighChar(c);
        return r != null;
    }

    /**
     * Pushes the encoded form of the given character to the given byte buffer.
     *
     * @param bb The byte buffer to write to.
     * @param c  The character to encode.
     * @return Whether the given unicode character is covered by this encoding.
     * If {@code false} is returned, nothing is pushed to the
     * byte buffer.
     */
    public boolean pushEncodedChar(final ByteBuffer bb, final char c) {

        if (c >= 0 && c < 128) {
            bb.put((byte) c);
            return true;
        }

        final Simple8BitChar r = this.encodeHighChar(c);
        if (r == null) {
            return false;
        }
        bb.put(r.code);
        return true;
    }

    /**
     * @param c A unicode character in the range from 0x0080 to 0x7f00
     * @return A Simple8BitChar, if this character is covered by this encoding.
     * A {@code null} value is returned, if this character is not
     * covered by this encoding.
     */
    private Simple8BitChar encodeHighChar(final char c) {
        // for performance an simplicity, yet another reincarnation of
        // binary search...
        int i0 = 0;
        int i1 = this.reverseMapping.size();

        while (i1 > i0) {

            final int i = i0 + (i1 - i0) / 2;

            final Simple8BitChar m = this.reverseMapping.get(i);

            if (m.unicode == c) {
                return m;
            }

            if (m.unicode < c) {
                i0 = i + 1;
            } else {
                i1 = i;
            }
        }

        if (i0 >= this.reverseMapping.size()) {
            return null;
        }

        final Simple8BitChar r = this.reverseMapping.get(i0);

        if (r.unicode != c) {
            return null;
        }

        return r;
    }

    /**
     * @see org.apache.tools.zip.ZipEncoding#canEncode(String)
     */
    public boolean canEncode(final String name) {

        for (int i = 0; i < name.length(); ++i) {

            final char c = name.charAt(i);

            if (!this.canEncodeChar(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.apache.tools.zip.ZipEncoding#encode(String)
     */
    public ByteBuffer encode(final String name) {
        ByteBuffer out = ByteBuffer.allocate(name.length()
                + 6 + (name.length() + 1) / 2);

        for (int i = 0; i < name.length(); ++i) {
            final char c = name.charAt(i);

            if (out.remaining() < 6) {
                out = AbstractZipEncodingHelper.growBuffer(out, out.position() + 6);
            }

            if (!this.pushEncodedChar(out, c)) {
                AbstractZipEncodingHelper.appendSurrogate(out, c);
            }
        }

        AbstractZipEncodingHelper.prepareBufferForRead(out);
        return out;
    }

    /**
     * @see ZipEncoding#decode(byte[])
     */
    public String decode(final byte[] data) throws IOException {
        final char[] ret = new char[data.length];

        for (int i = 0; i < data.length; ++i) {
            ret[i] = this.decodeByte(data[i]);
        }

        return new String(ret);
    }

    /**
     * A character entity, which is put to the reverse mapping table
     * of a simple encoding.
     */
    private static final class Simple8BitChar implements Comparable<Simple8BitChar> {
        public final char unicode;
        public final byte code;

        Simple8BitChar(final byte code, final char unicode) {
            this.code = code;
            this.unicode = unicode;
        }

        public int compareTo(final Simple8BitChar a) {
            return this.unicode - a.unicode;
        }

        @Override
        public String toString() {
            return "0x" + Integer.toHexString(0xffff & unicode)
                    + "->0x" + Integer.toHexString(0xff & code);
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof Simple8BitChar) {
                final Simple8BitChar other = (Simple8BitChar) o;
                return unicode == other.unicode && code == other.code;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return unicode;
        }
    }


}
