package com.chua.common.support.io.inner.stream;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.tar.TarEntry;
import com.chua.common.support.file.tar.TarInputStream;
import com.chua.common.support.utils.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * zip
 * @author CH
 */
@Spi({"tar.gz"})
public class TarGzStreamInnerCompressInputStream extends TarStreamInnerCompressInputStream {

    public TarGzStreamInnerCompressInputStream(InputStream inputStream, String name) {
        super(inputStream, name);
    }


    @Override
    protected InputStream getTarInputStream() throws IOException {
        return new GZIPInputStream(inputStream);
    }
}
