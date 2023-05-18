package com.chua.common.support.io.inner.stream;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.xz.XZInputStream;
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
@Spi({"xz"})
public class XzStreamInnerCompressInputStream extends AbstractStreamInnerCompressInputStream {

    public XzStreamInnerCompressInputStream(InputStream inputStream, String name) {
        super(inputStream, name);
    }

    @Override
    public InputStream createStream() throws IOException {
        return new XZInputStream(inputStream);
    }

}
