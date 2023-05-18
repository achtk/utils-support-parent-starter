package com.chua.common.support.io.inner.stream;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.xz.XZInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * zip
 * @author CH
 */
@Spi({"gz"})
public class GzipStreamInnerCompressInputStream extends AbstractStreamInnerCompressInputStream {

    public GzipStreamInnerCompressInputStream(InputStream inputStream, String name) {
        super(inputStream, name);
    }

    @Override
    public InputStream createStream() throws IOException {
        return new GZIPInputStream(inputStream);
    }

}
