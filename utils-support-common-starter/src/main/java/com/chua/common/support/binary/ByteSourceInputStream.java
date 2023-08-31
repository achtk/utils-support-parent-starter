package com.chua.common.support.binary;

import com.chua.common.support.constant.NumberConstant;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 流
 *
 * @author CH
 * @since 2022-01-14
 */
public class ByteSourceInputStream extends BaseByteSource {
    private static final int BLOCK_SIZE = 1024;

    private final InputStream is;
    private CacheBlock cacheHead;
    private byte[] readBuffer;
    private long streamLength = -1;

    public ByteSourceInputStream(final InputStream is) {
        this(is, "");
    }

    public ByteSourceInputStream(final InputStream is, final String filename) {
        super(filename);
        this.is = new BufferedInputStream(is);
    }

    private class CacheBlock {
        public final byte[] bytes;
        private CacheBlock next;
        private boolean triedNext;

        public CacheBlock(final byte[] bytes) {
            this.bytes = bytes;
        }

        public CacheBlock getNext() throws IOException {
            if (null != next) {
                return next;
            }
            if (triedNext) {
                return null;
            }
            triedNext = true;
            next = readBlock();
            return next;
        }

    }

    private CacheBlock readBlock() throws IOException {
        if (null == readBuffer) {
            readBuffer = new byte[BLOCK_SIZE];
        }

        final int read = is.read(readBuffer);
        if (read < 1) {
            return null;
        } else if (read < BLOCK_SIZE) {
            // return a copy.
            final byte[] result = new byte[read];
            System.arraycopy(readBuffer, 0, result, 0, read);
            return new CacheBlock(result);
        } else {
            // return current buffer.
            final byte[] result = readBuffer;
            readBuffer = null;
            return new CacheBlock(result);
        }
    }

    private CacheBlock getFirstBlock() throws IOException {
        if (null == cacheHead) {
            cacheHead = readBlock();
        }
        return cacheHead;
    }

    private class CacheReadingInputStream extends InputStream {
        private CacheBlock block;
        private boolean readFirst;
        private int blockIndex;

        @Override
        public int read() throws IOException {
            if (null == block) {
                if (readFirst) {
                    return -1;
                }
                block = getFirstBlock();
                readFirst = true;
            }

            if (block != null && blockIndex >= block.bytes.length) {
                block = block.getNext();
                blockIndex = 0;
            }

            if (null == block) {
                return -1;
            }

            if (blockIndex >= block.bytes.length) {
                return -1;
            }

            return 0xff & block.bytes[blockIndex++];
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            // first section copied verbatim from InputStream
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0)
                    || ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }

            // optimized block read

            if (null == block) {
                if (readFirst) {
                    return -1;
                }
                block = getFirstBlock();
                readFirst = true;
            }

            if (block != null && blockIndex >= block.bytes.length) {
                block = block.getNext();
                blockIndex = 0;
            }

            if (null == block) {
                return -1;
            }

            if (blockIndex >= block.bytes.length) {
                return -1;
            }

            final int readSize = Math.min(len, block.bytes.length - blockIndex);
            System.arraycopy(block.bytes, blockIndex, b, off, readSize);
            blockIndex += readSize;
            return readSize;
        }

        @Override
        public long skip(final long n) throws IOException {

            long remaining = n;

            if (n <= 0) {
                return 0;
            }

            while (remaining > 0) {
                // read the first block
                if (null == block) {
                    if (readFirst) {
                        return -1;
                    }
                    block = getFirstBlock();
                    readFirst = true;
                }

                // get next block
                if (block != null && blockIndex >= block.bytes.length) {
                    block = block.getNext();
                    blockIndex = 0;
                }

                if (null == block) {
                    break;
                }

                if (blockIndex >= block.bytes.length) {
                    break;
                }

                final int readSize = Math.min((int) Math.min(BLOCK_SIZE, remaining), block.bytes.length - blockIndex);

                blockIndex += readSize;
                remaining -= readSize;
            }

            return n - remaining;
        }

    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new CacheReadingInputStream();
    }

    @Override
    public byte[] getBlock(final long blockStart, final int blockLength) throws IOException {
        // We include a separate check for int overflow.
        if ((blockStart < 0) || (blockLength < 0)
                || (blockStart + blockLength < 0)
                || (blockStart + blockLength > streamLength)) {
            throw new IOException("Could not read block (block start: "
                    + blockStart + ", block length: " + blockLength
                    + ", data length: " + streamLength + ").");
        }

        final InputStream cis = getInputStream();
        Binarys.skipBytes(cis, blockStart);

        final byte[] bytes = new byte[blockLength];
        int total = 0;
        while (true) {
            final int read = cis.read(bytes, total, bytes.length - total);
            if (read < 1) {
                throw new IOException("Could not read block.");
            }
            total += read;
            if (total >= blockLength) {
                return bytes;
            }
        }
    }

    @Override
    public long getLength() throws IOException {
        if (streamLength >= 0) {
            return streamLength;
        }

        long result;
        try (InputStream cis = getInputStream()) {
            result = 0;
            long skipped;
            while ((skipped = cis.skip(NumberConstant.K)) > 0) {
                result += skipped;
            }
        }
        streamLength = result;
        return result;
    }

    @Override
    public byte[] getAll() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        CacheBlock block = getFirstBlock();
        while (block != null) {
            baos.write(block.bytes);
            block = block.getNext();
        }
        return baos.toByteArray();
    }

    @Override
    public String getDescription() {
        return "Inputstream: '" + getFilename() + "'";
    }

}
