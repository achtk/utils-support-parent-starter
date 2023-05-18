package com.chua.common.support.io;


import com.chua.common.support.io.inner.SimpleFileInnerCompressInputStream;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 压缩输入流
 *
 * @author CH
 */
public class CompressInputStream extends InputStream {
    private final String resource;
    private final String file;
    private FileInnerCompressInputStream stream;

    public CompressInputStream(Resource resource, String file) {
        this(resource.getUrl(), file);
    }

    public CompressInputStream(URL resource, String file) {
        this(resource.getFile(), file);
    }

    public CompressInputStream(File resource, String file) {
        this(resource.getAbsolutePath(), file);
    }

    public CompressInputStream(String resource, String file) {
        this.resource = resource;
        this.file = file;
        this.stream = ServiceProvider.of(FileInnerCompressInputStream.class)
                .getExtension(FileUtils.getSimpleExtension(resource));
        if (null == stream) {
            this.stream = new SimpleFileInnerCompressInputStream();
        }
        this.stream.source(resource, file);
    }

    @Override
    public int read() throws IOException {
        if (null == stream) {
            return 0;
        }
        return stream.getInputStream().available();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return stream.getInputStream().read(b, off, len);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        stream.getInputStream().close();
    }
}
