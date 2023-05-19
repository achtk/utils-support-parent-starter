package com.chua.apache.support.io;

import com.chua.common.support.annotations.SpiCondition;
import com.chua.common.support.io.FileInnerCompressInputStream;
import com.chua.common.support.io.InnerCompressInputStream;
import com.chua.common.support.io.inner.SimpleFileInnerCompressInputStream;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.IOException;

/**
 * bz2
 *
 * @author CH
 */
@SpiCondition("org.apache.commons.compress.archivers.ArchiveEntry")
public class Bz2InnerCompressInputStream extends AbstractApacheInnerCompressInputStream {

    @Override
    ArchiveInputStream createArchiveInputStream() throws IOException {
        SimpleFileInnerCompressInputStream simpleInnerCompressInputStream = new SimpleFileInnerCompressInputStream();
        FileInnerCompressInputStream innerCompressInputStream = simpleInnerCompressInputStream.source(parent, file);
        return new TarArchiveInputStream(new BZip2CompressorInputStream(innerCompressInputStream.getInputStream()));
    }

}
