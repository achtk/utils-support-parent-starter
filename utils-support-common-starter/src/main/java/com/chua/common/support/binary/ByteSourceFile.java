package com.chua.common.support.binary;

import java.io.*;

/**
 * 文件
 *
 * @author CH
 * @since 2022-01-14
 */
public class ByteSourceFile extends BaseByteSource {
    private final File file;

    public ByteSourceFile(final File file) {
        super(file.getName());
        this.file = file;
    }

    public ByteSourceFile(final String file) {
        super(file);
        this.file = new File(file);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    @Override
    public byte[] getBlock(final long start, final int length) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            // We include a separate check for int overflow.
            if ((start < 0) || (length < 0) || (start + length < 0)
                    || (start + length > raf.length())) {
                throw new IOException("Could not read block (block start: "
                        + start + ", block length: " + length
                        + ", data length: " + raf.length() + ").");
            }

            return Binarys.getRafBytes(raf, start, length,
                    "Could not read value from file");
        }
    }

    @Override
    public long getLength() {
        return file.length();
    }

    @Override
    public byte[] getAll() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (InputStream is = getInputStream()) {
            final byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        }
    }

    @Override
    public String getDescription() {
        return "File: '" + file.getAbsolutePath() + "'";
    }


    public File toFile() {
        return file;
    }
}
