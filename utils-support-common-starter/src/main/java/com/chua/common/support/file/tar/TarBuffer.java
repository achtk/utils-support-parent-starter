package com.chua.common.support.file.tar;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * The TarBuffer class implements the tar archive concept
 * of a buffered input stream. This concept goes back to the
 * days of blocked tape drives and special io devices. In the
 * Java universe, the only real function that this class
 * performs is to ensure that files have the correct "block"
 * size, or other tars will complain.
 * <p>
 * You should never have a need to access this class directly.
 * TarBuffers are created by Tar IO Streams.
 *
 * @author Administrator
 */

public class TarBuffer {

    /**
     * Default record size
     */
    public static final int DEFAULT_RCDSIZE = (512);

    /**
     * Default block size
     */
    public static final int DEFAULT_BLKSIZE = (DEFAULT_RCDSIZE * 20);
    private final int blockSize;
    private final int recordSize;
    private final int recsPerBlock;
    private final byte[] blockBuffer;
    private InputStream inStream;
    private OutputStream outStream;
    private int currBlkIdx;
    private int currRecIdx;
    private boolean debug;

    /**
     * Constructor for a TarBuffer on an input stream.
     *
     * @param inStream the input stream to use
     */
    public TarBuffer(InputStream inStream) {
        this(inStream, TarBuffer.DEFAULT_BLKSIZE);
    }

    /**
     * Constructor for a TarBuffer on an input stream.
     *
     * @param inStream  the input stream to use
     * @param blockSize the block size to use
     */
    public TarBuffer(InputStream inStream, int blockSize) {
        this(inStream, blockSize, TarBuffer.DEFAULT_RCDSIZE);
    }

    /**
     * Constructor for a TarBuffer on an input stream.
     *
     * @param inStream   the input stream to use
     * @param blockSize  the block size to use
     * @param recordSize the record size to use
     */
    public TarBuffer(InputStream inStream, int blockSize, int recordSize) {
        this(inStream, null, blockSize, recordSize);
    }

    /**
     * Constructor for a TarBuffer on an output stream.
     *
     * @param outStream the output stream to use
     */
    public TarBuffer(OutputStream outStream) {
        this(outStream, TarBuffer.DEFAULT_BLKSIZE);
    }

    /**
     * Constructor for a TarBuffer on an output stream.
     *
     * @param outStream the output stream to use
     * @param blockSize the block size to use
     */
    public TarBuffer(OutputStream outStream, int blockSize) {
        this(outStream, blockSize, TarBuffer.DEFAULT_RCDSIZE);
    }

    /**
     * Constructor for a TarBuffer on an output stream.
     *
     * @param outStream  the output stream to use
     * @param blockSize  the block size to use
     * @param recordSize the record size to use
     */
    public TarBuffer(OutputStream outStream, int blockSize, int recordSize) {
        this(null, outStream, blockSize, recordSize);
    }

    /**
     * Private constructor to perform common setup.
     */
    private TarBuffer(InputStream inStream, OutputStream outStream, int blockSize, int recordSize) {
        this.inStream = inStream;
        this.outStream = outStream;
        this.debug = false;
        this.blockSize = blockSize;
        this.recordSize = recordSize;
        this.recsPerBlock = (this.blockSize / this.recordSize);
        this.blockBuffer = new byte[this.blockSize];

        if (this.inStream != null) {
            this.currBlkIdx = -1;
            this.currRecIdx = this.recsPerBlock;
        } else {
            this.currBlkIdx = 0;
            this.currRecIdx = 0;
        }
    }

    /**
     * Get the TAR Buffer's block size. Blocks consist of multiple records.
     *
     * @return the block size
     */
    public int getBlockSize() {
        return this.blockSize;
    }

    /**
     * Get the TAR Buffer's record size.
     *
     * @return the record size
     */
    public int getRecordSize() {
        return this.recordSize;
    }

    /**
     * Set the debugging flag for the buffer.
     *
     * @param debug If true, print debugging output.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Determine if an archive record indicate End of Archive. End of
     * archive is indicated by a record that consists entirely of null bytes.
     *
     * @param record The record data to check.
     * @return true if the record data is an End of Archive
     */
    public boolean isEofRecord(byte[] record) {
        for (int i = 0, sz = getRecordSize(); i < sz; ++i) {
            if (record[i] != 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Skip over a record on the input stream.
     *
     * @throws IOException on error
     */
    public void skipRecord() throws IOException {
        if (debug) {
            System.err.println("SkipRecord: recIdx = " + currRecIdx
                    + " blkIdx = " + currBlkIdx);
        }

        if (inStream == null) {
            throw new IOException("reading (via skip) from an output buffer");
        }

        if (currRecIdx >= recsPerBlock && !readBlock()) {
            return;    // UNDONE
        }

        currRecIdx++;
    }

    /**
     * Read a record from the input stream and return the data.
     *
     * @return The record data.
     * @throws IOException on error
     */
    public byte[] readRecord() throws IOException {
        if (debug) {
            System.err.println("ReadRecord: recIdx = " + currRecIdx
                    + " blkIdx = " + currBlkIdx);
        }

        if (inStream == null) {
            if (outStream == null) {
                throw new IOException("input buffer is closed");
            }
            throw new IOException("reading from an output buffer");
        }

        if (currRecIdx >= recsPerBlock && !readBlock()) {
            return null;
        }

        byte[] result = new byte[recordSize];

        System.arraycopy(blockBuffer,
                (currRecIdx * recordSize), result, 0,
                recordSize);

        currRecIdx++;

        return result;
    }

    /**
     * @return false if End-Of-File, else true
     */
    private boolean readBlock() throws IOException {
        if (debug) {
            System.err.println("ReadBlock: blkIdx = " + currBlkIdx);
        }

        if (inStream == null) {
            throw new IOException("reading from an output buffer");
        }

        currRecIdx = 0;

        int offset = 0;
        int bytesNeeded = blockSize;

        while (bytesNeeded > 0) {
            long numBytes = inStream.read(blockBuffer, offset,
                    bytesNeeded);

            //
            // NOTE
            // We have fit EOF, and the block is not full!
            //
            // This is a broken archive. It does not follow the standard
            // blocking algorithm. However, because we are generous, and
            // it requires little effort, we will simply ignore the error
            // and continue as if the entire block were read. This does
            // not appear to break anything upstream. We used to return
            // false in this case.
            //
            // Thanks to 'Yohann.Roussel@alcatel.fr' for this fix.
            //
            if (numBytes == -1) {
                if (offset == 0) {
                    // Ensure that we do not read gigabytes of zeros
                    // for a corrupt tar file.
                    // See https://issues.apache.org/bugzilla/show_bug.cgi?id=39924
                    return false;
                }
                // However, just leaving the unread portion of the buffer dirty does
                // cause problems in some cases.  This problem is described in
                // https://issues.apache.org/bugzilla/show_bug.cgi?id=29877
                //
                // The solution is to fill the unused portion of the buffer with zeros.

                Arrays.fill(blockBuffer, offset, offset + bytesNeeded, (byte) 0);

                break;
            }

            offset += numBytes;
            bytesNeeded -= numBytes;

            if (numBytes != blockSize) {
                if (debug) {
                    System.err.println("ReadBlock: INCOMPLETE READ "
                            + numBytes + " of " + blockSize
                            + " bytes read.");
                }
            }
        }

        currBlkIdx++;

        return true;
    }

    /**
     * Get the current block number, zero based.
     *
     * @return The current zero based block number.
     */
    public int getCurrentBlockNum() {
        return currBlkIdx;
    }

    /**
     * Get the current record number, within the current block, zero based.
     * Thus, current offset = (currentBlockNum * recsPerBlk) + currentRecNum.
     *
     * @return The current zero based record number.
     */
    public int getCurrentRecordNum() {
        return currRecIdx - 1;
    }

    /**
     * Write an archive record to the archive.
     *
     * @param record The record data to write to the archive.
     * @throws IOException on error
     */
    public void writeRecord(byte[] record) throws IOException {
        if (debug) {
            System.err.println("WriteRecord: recIdx = " + currRecIdx
                    + " blkIdx = " + currBlkIdx);
        }

        if (outStream == null) {
            if (inStream == null) {
                throw new IOException("Output buffer is closed");
            }
            throw new IOException("writing to an input buffer");
        }

        if (record.length != recordSize) {
            throw new IOException("record to write has length '"
                    + record.length
                    + "' which is not the record size of '"
                    + recordSize + "'");
        }

        if (currRecIdx >= recsPerBlock) {
            writeBlock();
        }

        System.arraycopy(record, 0, blockBuffer,
                (currRecIdx * recordSize),
                recordSize);

        currRecIdx++;
    }

    /**
     * Write an archive record to the archive, where the record may be
     * inside of a larger array buffer. The buffer must be "offset plus
     * record size" long.
     *
     * @param buf    The buffer containing the record data to write.
     * @param offset The offset of the record data within buf.
     * @throws IOException on error
     */
    public void writeRecord(byte[] buf, int offset) throws IOException {
        if (debug) {
            System.err.println("WriteRecord: recIdx = " + currRecIdx
                    + " blkIdx = " + currBlkIdx);
        }

        if (outStream == null) {
            if (inStream == null) {
                throw new IOException("Output buffer is closed");
            }
            throw new IOException("writing to an input buffer");
        }

        if ((offset + recordSize) > buf.length) {
            throw new IOException("record has length '" + buf.length
                    + "' with offset '" + offset
                    + "' which is less than the record size of '"
                    + recordSize + "'");
        }

        if (currRecIdx >= recsPerBlock) {
            writeBlock();
        }

        System.arraycopy(buf, offset, blockBuffer,
                (currRecIdx * recordSize),
                recordSize);

        currRecIdx++;
    }

    /**
     * Write a TarBuffer block to the archive.
     */
    private void writeBlock() throws IOException {
        if (debug) {
            System.err.println("WriteBlock: blkIdx = " + currBlkIdx);
        }

        if (outStream == null) {
            throw new IOException("writing to an input buffer");
        }

        outStream.write(blockBuffer, 0, blockSize);
        outStream.flush();

        currRecIdx = 0;
        currBlkIdx++;
        Arrays.fill(blockBuffer, (byte) 0);
    }

    /**
     * Flush the current data block if it has any data in it.
     */
    void flushBlock() throws IOException {
        if (debug) {
            System.err.println("TarBuffer.flushBlock() called.");
        }

        if (outStream == null) {
            throw new IOException("writing to an input buffer");
        }

        if (currRecIdx > 0) {
            writeBlock();
        }
    }

    /**
     * Close the TarBuffer. If this is an output buffer, also flush the
     * current block before closing.
     *
     * @throws IOException on error
     */
    public void close() throws IOException {
        if (debug) {
            System.err.println("TarBuffer.closeBuffer().");
        }

        if (outStream != null) {
            flushBlock();

            if (outStream != System.out
                    && outStream != System.err) {
                outStream.close();

                outStream = null;
            }
        } else if (inStream != null) {
            if (inStream != System.in) {
                inStream.close();
            }
            inStream = null;
        }
    }
}
