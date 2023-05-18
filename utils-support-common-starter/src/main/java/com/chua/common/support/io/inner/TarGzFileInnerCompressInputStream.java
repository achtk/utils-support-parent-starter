package com.chua.common.support.io.inner;


import com.chua.common.support.annotations.Spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;

/**
 * tar流
 *
 * @author CH
 */
@Spi("tar.gz")
public class TarGzFileInnerCompressInputStream extends TarFileInnerCompressInputStream {

    @Override
    protected InputStream getFileInputStream() throws IOException {
        return new GZIPInputStream(Files.newInputStream(new File(parent).toPath()));
    }
}
