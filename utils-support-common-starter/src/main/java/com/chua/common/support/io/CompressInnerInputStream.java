package com.chua.common.support.io;

import com.chua.common.support.spi.ServiceProvider;

import java.io.IOException;
import java.io.InputStream;

/**
 * 压缩流
 * @author CH
 */
public class CompressInnerInputStream extends InputStream {

    private final InputStream inputStream;

    private final String file;
    private final InnerCompressInputStream innerCompressInputStream;

    public CompressInnerInputStream(InputStream inputStream, String streamType, String file) {
        this.file = file;
        this.innerCompressInputStream = ServiceProvider.of(InnerCompressInputStream.class).getNewExtension(streamType, inputStream, file);
        try {
            this.inputStream = innerCompressInputStream.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int read() throws IOException {
        if (null == inputStream) {
            return 0;
        }
        return inputStream.available();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
