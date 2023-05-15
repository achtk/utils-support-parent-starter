package com.chua.common.support.file.zip;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * A ZipEncoding, which uses a java.nio {@link
 * Charset Charset} to encode names.
 *
 * <p>This implementation works for all cases under java-1.5 or
 * later. However, in java-1.4, some charsets don't have a java.nio
 * implementation, most notably the default ZIP encoding Cp437.</p>
 *
 * <p>The methods of this class are reentrant.</p>
 */
class NioZipEncoding implements ZipEncoding {
    private final Charset charset;

    /**
     * Construct an NIO based zip encoding, which wraps the given
     * charset.
     *
     * @param charset The NIO charset to wrap.
     */
    public NioZipEncoding(final Charset charset) {
        this.charset = charset;
    }

    /**
     * @see org.apache.tools.zip.ZipEncoding#canEncode(String)
     */
    public boolean canEncode(final String name) {
        final CharsetEncoder enc = this.charset.newEncoder();
        enc.onMalformedInput(CodingErrorAction.REPORT);
        enc.onUnmappableCharacter(CodingErrorAction.REPORT);

        return enc.canEncode(name);
    }

    /**
     * @see org.apache.tools.zip.ZipEncoding#encode(String)
     */
    public ByteBuffer encode(final String name) {
        final CharsetEncoder enc = this.charset.newEncoder();

        enc.onMalformedInput(CodingErrorAction.REPORT);
        enc.onUnmappableCharacter(CodingErrorAction.REPORT);

        final CharBuffer cb = CharBuffer.wrap(name);
        ByteBuffer out = ByteBuffer.allocate(name.length()
                + (name.length() + 1) / 2);

        while (cb.remaining() > 0) {
            final CoderResult res = enc.encode(cb, out, true);

            if (res.isUnmappable() || res.isMalformed()) {

                // write the unmappable characters in utf-16
                // pseudo-URL encoding style to ByteBuffer.
                if (res.length() * 6 > out.remaining()) {
                    out = AbstractZipEncodingHelper.growBuffer(out, out.position()
                            + res.length() * 6);
                }

                for (int i = 0; i < res.length(); ++i) {
                    AbstractZipEncodingHelper.appendSurrogate(out, cb.get());
                }

            } else if (res.isOverflow()) {

                out = AbstractZipEncodingHelper.growBuffer(out, 0);

            } else if (res.isUnderflow()) {

                enc.flush(out);
                break;

            }
        }

        AbstractZipEncodingHelper.prepareBufferForRead(out);

        return out;
    }

    /**
     * @see ZipEncoding#decode(byte[])
     */
    public String decode(final byte[] data) throws IOException {
        return this.charset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(data)).toString();
    }
}
