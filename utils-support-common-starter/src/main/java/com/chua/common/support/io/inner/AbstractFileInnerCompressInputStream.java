package com.chua.common.support.io.inner;

import com.chua.common.support.io.FileInnerCompressInputStream;
import com.chua.common.support.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 流
 *
 * @author CH
 */
public abstract class AbstractFileInnerCompressInputStream implements FileInnerCompressInputStream {

    final Object LOCK = new Object();

    protected String parent;
    protected String file;
    private InputStream inputStream;
    protected String password;

    @Override
    public InputStream getInputStream() throws IOException {
        if (null != inputStream) {
            return inputStream;
        }

        synchronized (LOCK) {
            if (null != inputStream) {
                return inputStream;
            }
            inputStream = createStream();
            return inputStream;
        }
    }

    /**
     * 流
     *
     * @return 流
     * @throws IOException ex
     */
    protected abstract InputStream createStream() throws IOException;

    @Override
    public void reset() {
        IoUtils.closeQuietly(inputStream);
        inputStream = null;
    }

    @Override
    public FileInnerCompressInputStream source(String parent, String file) {
        this.parent = parent;
        this.file = file;
        return this;
    }

    @Override
    public FileInnerCompressInputStream password(String password) {
        this.password = password;
        return this;
    }
}
