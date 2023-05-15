package com.chua.common.support.file.zip;


import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A fallback ZipEncoding, which uses a java.io means to encode names.
 *
 * <p>This implementation is not favorable for encodings other than
 * utf-8, because java.io encodes unmappable character as question
 * marks leading to unreadable ZIP entries on some operating
 * systems.</p>
 *
 * <p>Furthermore this implementation is unable to tell whether a
 * given name can be safely encoded or not.</p>
 *
 * <p>This implementation acts as a last resort implementation, when
 * neither {@link Simple8BitZipEncoding} nor {@link NioZipEncoding} is
 * available.</p>
 *
 * <p>The methods of this class are reentrant.</p>
 */
class FallbackZipEncoding implements ZipEncoding {
    private final String charset;

    /**
     * Construct a fallback zip encoding, which uses the platform's
     * default charset.
     */
    public FallbackZipEncoding() {
        this.charset = null;
    }

    /**
     * Construct a fallback zip encoding, which uses the given charset.
     *
     * @param charset The name of the charset or {@code null} for
     *                the platform's default character set.
     */
    public FallbackZipEncoding(final String charset) {
        this.charset = charset;
    }

    /**
     * @see ZipEncoding#canEncode(String)
     */
    public boolean canEncode(final String name) {
        return true;
    }

    /**
     * @see ZipEncoding#encode(String)
     */
    public ByteBuffer encode(final String name) throws IOException {
        if (this.charset == null) { // i.e. use default charset, see no-args constructor
            return ByteBuffer.wrap(name.getBytes());
        } else {
            return ByteBuffer.wrap(name.getBytes(this.charset));
        }
    }

    /**
     * @see ZipEncoding#decode(byte[])
     */
    public String decode(final byte[] data) throws IOException {
        if (this.charset == null) { // i.e. use default charset, see no-args constructor
            return new String(data);
        } else {
            return new String(data, this.charset);
        }
    }
}
