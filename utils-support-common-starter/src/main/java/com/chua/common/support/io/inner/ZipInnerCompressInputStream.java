package com.chua.common.support.io.inner;


import com.chua.common.support.annotations.Spi;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * zip 流
 *
 * @author CH
 */
@Spi("zip")
public class ZipInnerCompressInputStream extends AbstractInnerCompressInputStream {
    @Override
    protected InputStream createStream() throws IOException {
        ZipFile zipFile = new ZipFile(parent);
        ZipEntry zipEntry = zipFile.getEntry(file);
        return zipFile.getInputStream(zipEntry);
    }


}
