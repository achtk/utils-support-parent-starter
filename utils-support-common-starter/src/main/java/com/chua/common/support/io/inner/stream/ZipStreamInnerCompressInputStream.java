package com.chua.common.support.io.inner.stream;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.io.FileInnerCompressInputStream;
import com.chua.common.support.io.InnerCompressInputStream;
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
@Spi({"zip", "jar", "war"})
public class ZipStreamInnerCompressInputStream extends AbstractStreamInnerCompressInputStream {

    public ZipStreamInnerCompressInputStream(InputStream inputStream, String name) {
        super(inputStream, name);
    }

    @Override
    public InputStream createStream() throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry nextEntry = null;
            while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                if (name.equals(nextEntry.getName())) {
                    byte[] bytes = IoUtils.toByteArrayKeepOpen(zipInputStream);
                    return new ByteArrayInputStream(bytes);
                }
            }
        }
        return null;
    }

}
