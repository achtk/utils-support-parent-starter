package com.chua.common.support.file.xz;

import com.chua.common.support.file.xz.check.BaseCheck;
import com.chua.common.support.file.xz.common.DecoderUtil;
import com.chua.common.support.file.xz.common.StreamFlags;
import com.chua.common.support.file.xz.index.IndexHash;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Decompresses exactly one Xz Stream in streamed mode (no seeking).
 * The decompression stops after the first Xz Stream has been decompressed,
 * and the read position in the input stream is left at the first byte
 * after the end of the Xz Stream. This can be useful when Xz data has
 * been stored inside some other file format or protocol.
 * <p>
 * Unless you know what you are doing, don't use this class to decompress
 * standalone .xz files. For that purpose, use <code>XzInputStream</code>.
 *
 * <h2>When uncompressed size is known beforehand</h2>
 * <p>
 * If you are decompressing complete Xz streams and your application knows
 * exactly how much uncompressed data there should be, it is good to try
 * reading one more byte by calling <code>read()</code> and checking
 * that it returns <code>-1</code>. This way the decompressor will parse the
 * file footers and verify the integrity checks, giving the caller more
 * confidence that the uncompressed data is valid.
 *
 * @see XzInputStream
 */
public class SingleXzInputStream extends InputStream {
    private InputStream in;
    private final ArrayCache arrayCache;
    private final int memoryLimit;
    private final StreamFlags streamHeaderFlags;
    private final BaseCheck check;
    private final boolean verifyCheck;
    private BlockInputStream blockDecoder = null;
    private final IndexHash indexHash = new IndexHash();
    private boolean endReached = false;
    private IOException exception = null;

    private final byte[] tempBuf = new byte[1];

    /**
     * Reads the Stream Header into a buffer.
     * This is a helper function for the constructors.
     */
    private static byte[] readStreamHeader(InputStream in) throws IOException {
        byte[] streamHeader = new byte[DecoderUtil.STREAM_HEADER_SIZE];
        new DataInputStream(in).readFully(streamHeader);
        return streamHeader;
    }

    /**
     * Creates a new Xz decompressor that decompresses exactly one
     * Xz Stream from <code>in</code> without a memory usage limit.
     * <p>
     * This constructor reads and parses the Xz Stream Header (12 bytes)
     * from <code>in</code>. The header of the first Block is not read
     * until <code>read</code> is called.
     *
     * @param       in          input stream from which Xz-compressed
     *                          data is read
     *
     * @throws      XzFormatException
     *                          input is not in the Xz format
     *
     * @throws      CorruptedInputException
     *                          Xz header CRC32 doesn't match
     *
     * @throws      UnsupportedOptionsException
     *                          Xz header is valid but specifies options
     *                          not supported by this implementation
     *
     * @throws      EOFException
     *                          less than 12 bytes of input was available
     *                          from <code>in</code>
     *
     * @throws      IOException may be thrown by <code>in</code>
     */
    public SingleXzInputStream(InputStream in) throws IOException {
        this(in, -1);
    }

    /**
     * Creates a new Xz decompressor that decompresses exactly one
     * Xz Stream from <code>in</code> without a memory usage limit.
     * <p>
     * This is identical to <code>SingleXzInputStream(InputStream)</code>
     * except that this also takes the <code>arrayCache</code> argument.
     *
     * @param       in          input stream from which Xz-compressed
     *                          data is read
     *
     * @param       arrayCache  cache to be used for allocating large arrays
     *
     * @throws      XzFormatException
     *                          input is not in the Xz format
     *
     * @throws      CorruptedInputException
     *                          Xz header CRC32 doesn't match
     *
     * @throws      UnsupportedOptionsException
     *                          Xz header is valid but specifies options
     *                          not supported by this implementation
     *
     * @throws      EOFException
     *                          less than 12 bytes of input was available
     *                          from <code>in</code>
     *
     * @throws      IOException may be thrown by <code>in</code>
     *
     * @since 1.7
     */
    public SingleXzInputStream(InputStream in, ArrayCache arrayCache)
            throws IOException {
        this(in, -1, arrayCache);
    }

    /**
     * Creates a new Xz decompressor that decompresses exactly one
     * Xz Stream from <code>in</code> with an optional memory usage limit.
     * <p>
     * This is identical to <code>SingleXzInputStream(InputStream)</code>
     * except that this also takes the <code>memoryLimit</code> argument.
     *
     * @param       in          input stream from which Xz-compressed
     *                          data is read
     *
     * @param       memoryLimit memory usage limit in kibibytes (KiB)
     *                          or <code>-1</code> to impose no
     *                          memory usage limit
     *
     * @throws      XzFormatException
     *                          input is not in the Xz format
     *
     * @throws      CorruptedInputException
     *                          Xz header CRC32 doesn't match
     *
     * @throws      UnsupportedOptionsException
     *                          Xz header is valid but specifies options
     *                          not supported by this implementation
     *
     * @throws      EOFException
     *                          less than 12 bytes of input was available
     *                          from <code>in</code>
     *
     * @throws      IOException may be thrown by <code>in</code>
     */
    public SingleXzInputStream(InputStream in, int memoryLimit)
            throws IOException {
        this(in, memoryLimit, true);
    }

    /**
     * Creates a new Xz decompressor that decompresses exactly one
     * Xz Stream from <code>in</code> with an optional memory usage limit.
     * <p>
     * This is identical to <code>SingleXzInputStream(InputStream)</code>
     * except that this also takes the <code>memoryLimit</code> and
     * <code>arrayCache</code> arguments.
     *
     * @param       in          input stream from which Xz-compressed
     *                          data is read
     *
     * @param       memoryLimit memory usage limit in kibibytes (KiB)
     *                          or <code>-1</code> to impose no
     *                          memory usage limit
     *
     * @param       arrayCache  cache to be used for allocating large arrays
     *
     * @throws      XzFormatException
     *                          input is not in the Xz format
     *
     * @throws      CorruptedInputException
     *                          Xz header CRC32 doesn't match
     *
     * @throws      UnsupportedOptionsException
     *                          Xz header is valid but specifies options
     *                          not supported by this implementation
     *
     * @throws      EOFException
     *                          less than 12 bytes of input was available
     *                          from <code>in</code>
     *
     * @throws      IOException may be thrown by <code>in</code>
     *
     * @since 1.7
     */
    public SingleXzInputStream(InputStream in, int memoryLimit,
                               ArrayCache arrayCache) throws IOException {
        this(in, memoryLimit, true, arrayCache);
    }

    /**
     * Creates a new Xz decompressor that decompresses exactly one
     * Xz Stream from <code>in</code> with an optional memory usage limit
     * and ability to disable verification of integrity checks.
     * <p>
     * This is identical to <code>SingleXzInputStream(InputStream,int)</code>
     * except that this also takes the <code>verifyCheck</code> argument.
     * <p>
     * Note that integrity check verification should almost never be disabled.
     * Possible reasons to disable integrity check verification:
     * <ul>
     *   <li>Trying to recover data from a corrupt .xz file.</li>
     *   <li>Speeding up decompression. This matters mostly with SHA-256
     *   or with files that have compressed extremely well. It's recommended
     *   that integrity checking isn't disabled for performance reasons
     *   unless the file integrity is verified externally in some other
     *   way.</li>
     * </ul>
     * <p>
     * <code>verifyCheck</code> only affects the integrity check of
     * the actual compressed data. The CRC32 fields in the headers
     * are always verified.
     *
     * @param       in          input stream from which Xz-compressed
     *                          data is read
     *
     * @param       memoryLimit memory usage limit in kibibytes (KiB)
     *                          or <code>-1</code> to impose no
     *                          memory usage limit
     *
     * @param       verifyCheck if <code>true</code>, the integrity checks
     *                          will be verified; this should almost never
     *                          be set to <code>false</code>
     *
     * @throws      XzFormatException
     *                          input is not in the Xz format
     *
     * @throws      CorruptedInputException
     *                          Xz header CRC32 doesn't match
     *
     * @throws      UnsupportedOptionsException
     *                          Xz header is valid but specifies options
     *                          not supported by this implementation
     *
     * @throws      EOFException
     *                          less than 12 bytes of input was available
     *                          from <code>in</code>
     *
     * @throws      IOException may be thrown by <code>in</code>
     *
     * @since 1.6
     */
    public SingleXzInputStream(InputStream in, int memoryLimit,
                               boolean verifyCheck) throws IOException {
        this(in, memoryLimit, verifyCheck, ArrayCache.getDefaultCache());
    }

    /**
     * Creates a new Xz decompressor that decompresses exactly one
     * Xz Stream from <code>in</code> with an optional memory usage limit
     * and ability to disable verification of integrity checks.
     * <p>
     * This is identical to
     * <code>SingleXzInputStream(InputStream,int,boolean)</code>
     * except that this also takes the <code>arrayCache</code> argument.
     *
     * @param       in          input stream from which Xz-compressed
     *                          data is read
     *
     * @param       memoryLimit memory usage limit in kibibytes (KiB)
     *                          or <code>-1</code> to impose no
     *                          memory usage limit
     *
     * @param       verifyCheck if <code>true</code>, the integrity checks
     *                          will be verified; this should almost never
     *                          be set to <code>false</code>
     *
     * @param       arrayCache  cache to be used for allocating large arrays
     *
     * @throws      XzFormatException
     *                          input is not in the Xz format
     *
     * @throws      CorruptedInputException
     *                          Xz header CRC32 doesn't match
     *
     * @throws      UnsupportedOptionsException
     *                          Xz header is valid but specifies options
     *                          not supported by this implementation
     *
     * @throws      EOFException
     *                          less than 12 bytes of input was available
     *                          from <code>in</code>
     *
     * @throws      IOException may be thrown by <code>in</code>
     *
     * @since 1.7
     */
    public SingleXzInputStream(InputStream in, int memoryLimit,
                               boolean verifyCheck, ArrayCache arrayCache)
            throws IOException {
        this(in, memoryLimit, verifyCheck, readStreamHeader(in), arrayCache);
    }

    SingleXzInputStream(InputStream in, int memoryLimit, boolean verifyCheck,
                        byte[] streamHeader, ArrayCache arrayCache)
            throws IOException {
        this.arrayCache = arrayCache;
        this.in = in;
        this.memoryLimit = memoryLimit;
        this.verifyCheck = verifyCheck;
        streamHeaderFlags = DecoderUtil.decodeStreamHeader(streamHeader);
        check = BaseCheck.getInstance(streamHeaderFlags.checkType);
    }

    /**
     * Gets the ID of the integrity check used in this Xz Stream.
     *
     * @return      the Check ID specified in the Xz Stream Header
     */
    public int getCheckType() {
        return streamHeaderFlags.checkType;
    }

    /**
     * Gets the name of the integrity check used in this Xz Stream.
     *
     * @return      the name of the check specified in the Xz Stream Header
     */
    public String getCheckName() {
        return check.getName();
    }

    /**
     * Decompresses the next byte from this input stream.
     * <p>
     * Reading lots of data with <code>read()</code> from this input stream
     * may be inefficient. Wrap it in {@link java.io.BufferedInputStream}
     * if you need to read lots of data one byte at a time.
     *
     * @return      the next decompressed byte, or <code>-1</code>
     *              to indicate the end of the compressed stream
     *
     * @throws      CorruptedInputException
     * @throws      UnsupportedOptionsException
     * @throws      MemoryLimitException
     *
     * @throws      XzException if the stream has been closed
     *
     * @throws      EOFException
     *                          compressed input is truncated or corrupt
     *
     * @throws      IOException may be thrown by <code>in</code>
     */
    public int read() throws IOException {
        return read(tempBuf, 0, 1) == -1 ? -1 : (tempBuf[0] & 0xFF);
    }

    /**
     * Decompresses into an array of bytes.
     * <p>
     * If <code>len</code> is zero, no bytes are read and <code>0</code>
     * is returned. Otherwise this will try to decompress <code>len</code>
     * bytes of uncompressed data. Less than <code>len</code> bytes may
     * be read only in the following situations:
     * <ul>
     *   <li>The end of the compressed data was reached successfully.</li>
     *   <li>An error is detected after at least one but less <code>len</code>
     *       bytes have already been successfully decompressed.
     *       The next call with non-zero <code>len</code> will immediately
     *       throw the pending exception.</li>
     *   <li>An exception is thrown.</li>
     * </ul>
     *
     * @param       buf         target buffer for uncompressed data
     * @param       off         start offset in <code>buf</code>
     * @param       len         maximum number of uncompressed bytes to read
     *
     * @return      number of bytes read, or <code>-1</code> to indicate
     *              the end of the compressed stream
     *
     * @throws      CorruptedInputException
     * @throws      UnsupportedOptionsException
     * @throws      MemoryLimitException
     *
     * @throws      XzException if the stream has been closed
     *
     * @throws      EOFException
     *                          compressed input is truncated or corrupt
     *
     * @throws      IOException may be thrown by <code>in</code>
     */
    public int read(byte[] buf, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len < 0 || off + len > buf.length)
            throw new IndexOutOfBoundsException();

        if (len == 0)
            return 0;

        if (in == null)
            throw new XzException("Stream closed");

        if (exception != null)
            throw exception;

        if (endReached)
            return -1;

        int size = 0;

        try {
            while (len > 0) {
                if (blockDecoder == null) {
                    try {
                        blockDecoder = new BlockInputStream(
                                in, check, verifyCheck, memoryLimit, -1, -1,
                                arrayCache);
                    } catch (IndexIndicatorException e) {
                        indexHash.validate(in);
                        validateStreamFooter();
                        endReached = true;
                        return size > 0 ? size : -1;
                    }
                }

                int ret = blockDecoder.read(buf, off, len);

                if (ret > 0) {
                    size += ret;
                    off += ret;
                    len -= ret;
                } else if (ret == -1) {
                    indexHash.add(blockDecoder.getUnpaddedSize(),
                                  blockDecoder.getUncompressedSize());
                    blockDecoder = null;
                }
            }
        } catch (IOException e) {
            exception = e;
            if (size == 0)
                throw e;
        }

        return size;
    }

    private void validateStreamFooter() throws IOException {
        byte[] buf = new byte[DecoderUtil.STREAM_HEADER_SIZE];
        new DataInputStream(in).readFully(buf);
        StreamFlags streamFooterFlags = DecoderUtil.decodeStreamFooter(buf);

        if (!DecoderUtil.areStreamFlagsEqual(streamHeaderFlags,
                                             streamFooterFlags)
                || indexHash.getIndexSize() != streamFooterFlags.backwardSize)
            throw new CorruptedInputException(
                    "Xz Stream Footer does not match Stream Header");
    }

    /**
     * Returns the number of uncompressed bytes that can be read
     * without blocking. The value is returned with an assumption
     * that the compressed input data will be valid. If the compressed
     * data is corrupt, <code>CorruptedInputException</code> may get
     * thrown before the number of bytes claimed to be available have
     * been read from this input stream.
     *
     * @return      the number of uncompressed bytes that can be read
     *              without blocking
     */
    public int available() throws IOException {
        if (in == null)
            throw new XzException("Stream closed");

        if (exception != null)
            throw exception;

        return blockDecoder == null ? 0 : blockDecoder.available();
    }

    /**
     * Closes the stream and calls <code>in.close()</code>.
     * If the stream was already closed, this does nothing.
     * <p>
     * This is equivalent to <code>close(true)</code>.
     *
     * @throws  IOException if thrown by <code>in.close()</code>
     */
    public void close() throws IOException {
        close(true);
    }

    /**
     * Closes the stream and optionally calls <code>in.close()</code>.
     * If the stream was already closed, this does nothing.
     * If <code>close(false)</code> has been called, a further
     * call of <code>close(true)</code> does nothing (it doesn't call
     * <code>in.close()</code>).
     * <p>
     * If you don't want to close the underlying <code>InputStream</code>,
     * there is usually no need to worry about closing this stream either;
     * it's fine to do nothing and let the garbage collector handle it.
     * However, if you are using {@link ArrayCache}, <code>close(false)</code>
     * can be useful to put the allocated arrays back to the cache without
     * closing the underlying <code>InputStream</code>.
     * <p>
     * Note that if you successfully reach the end of the stream
     * (<code>read</code> returns <code>-1</code>), the arrays are
     * automatically put back to the cache by that <code>read</code> call. In
     * this situation <code>close(false)</code> is redundant (but harmless).
     *
     * @throws  IOException if thrown by <code>in.close()</code>
     *
     * @since 1.7
     */
    public void close(boolean closeInput) throws IOException {
        if (in != null) {
            if (blockDecoder != null) {
                blockDecoder.close();
                blockDecoder = null;
            }

            try {
                if (closeInput)
                    in.close();
            } finally {
                in = null;
            }
        }
    }
}
