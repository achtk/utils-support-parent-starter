package com.chua.common.support.io.inner;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.xz.XZInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * tar流
 *
 * @author CH
 */
@Spi("tar.xz")
public class TarXzInnerCompressInputStream extends TarInnerCompressInputStream {

    @Override
    protected InputStream getFileInputStream() throws IOException {
        return new XZInputStream(Files.newInputStream(new File(parent).toPath()));
    }
}
