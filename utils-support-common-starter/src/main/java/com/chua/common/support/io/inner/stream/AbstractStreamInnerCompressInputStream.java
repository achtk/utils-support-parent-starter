package com.chua.common.support.io.inner.stream;

import com.chua.common.support.io.FileInnerCompressInputStream;
import com.chua.common.support.io.InnerCompressInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * zip
 * @author CH
 */
public abstract class AbstractStreamInnerCompressInputStream implements InnerCompressInputStream {

    protected InputStream inputStream;
    protected InputStream newInputStream;
    protected final String name;
    final Object LOCK = new Object();

    public AbstractStreamInnerCompressInputStream(InputStream inputStream, String name) {
        this.inputStream = inputStream;
        this.name = name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (null != newInputStream) {
            return newInputStream;
        }

        synchronized (LOCK) {
            if (null != newInputStream) {
                return newInputStream;
            }
            newInputStream = createStream();
            return newInputStream;
        }
    }

    /**
     * 流
     *
     * @return 流
     * @throws IOException ex
     */
    protected abstract InputStream createStream() throws IOException;

}
