package com.chua.common.support.io.inner.stream;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.tar.TarEntry;
import com.chua.common.support.file.tar.TarInputStream;
import com.chua.common.support.utils.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * zip
 * @author CH
 */
@Spi({"tar"})
public class TarStreamInnerCompressInputStream extends AbstractStreamInnerCompressInputStream {

    public TarStreamInnerCompressInputStream(InputStream inputStream, String name) {
        super(inputStream, name);
    }

    @Override
    public InputStream createStream() throws IOException {
        try (TarInputStream tarInputStream = new TarInputStream(getTarInputStream())) {
            TarEntry nextEntry = null;
            while ((nextEntry = tarInputStream.getNextEntry()) != null) {
                if (name.equals(nextEntry.getName())) {
                    byte[] bytes = IoUtils.toByteArrayKeepOpen(tarInputStream);
                    return new ByteArrayInputStream(bytes);
                }
            }
        }
        return null;
    }

    protected InputStream getTarInputStream() throws IOException {
        return inputStream;
    }

}
