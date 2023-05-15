package com.chua.common.support.binary;

import java.io.IOException;
import java.io.InputStream;

/**
 * 二进制文件
 *
 * @author CH
 * @since 2022-01-14
 */
public abstract class AbstractByteSource {
    private final String filename;

    public AbstractByteSource(final String filename) {
        this.filename = filename;
    }

    public final InputStream getInputStream(final long start) throws IOException {
        InputStream is = null;
        boolean succeeded = false;
        try {
            is = getInputStream();
            Binarys.skipBytes(is, start);
            succeeded = true;
        } finally {
            if (!succeeded && is != null) {
                is.close();
            }
        }
        return is;
    }

    /**
     * 获取流
     *
     * @return 流
     * @throws IOException ex
     */
    public abstract InputStream getInputStream() throws IOException;

    /**
     * 获取流
     *
     * @return 流
     * @throws IOException ex
     */
    public byte[] getBlock(final int start, final int length) throws IOException {
        return getBlock(0xFFFFffffL & start, length);
    }

    /**
     * 获取流
     *
     * @param start  start
     * @param length length
     * @return 流
     * @throws IOException ex
     */
    public abstract byte[] getBlock(long start, int length) throws IOException;

    /**
     * 获取流
     *
     * @return 流
     * @throws IOException ex
     */
    public abstract byte[] getAll() throws IOException;

    /**
     * 获取长度
     *
     * @return 流
     * @throws IOException ex
     */
    public abstract long getLength() throws IOException;

    /**
     * 描述
     *
     * @return 流
     */
    public abstract String getDescription();

    /**
     * 名称
     *
     * @return 流
     */
    public final String getFilename() {
        return filename;
    }
}
