package com.chua.apache.support.io;

import com.chua.common.support.annotations.SpiCondition;
import com.chua.common.support.io.FileInnerCompressInputStream;
import com.chua.common.support.io.InnerCompressInputStream;
import com.chua.common.support.io.inner.SimpleFileInnerCompressInputStream;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.IOException;

/**
 * tar.gz
 *
 * @author CH
 */
@SpiCondition("org.apache.commons.compress.archivers.ArchiveEntry")
public class TarGzInnerCompressInputStream extends AbstractApacheInnerCompressInputStream {

    @Override
    ArchiveInputStream createArchiveInputStream() throws IOException {
        SimpleFileInnerCompressInputStream simpleInnerCompressInputStream = new SimpleFileInnerCompressInputStream();
        FileInnerCompressInputStream innerCompressInputStream = simpleInnerCompressInputStream.source(parent, file);
        return new TarArchiveInputStream(new GzipCompressorInputStream(innerCompressInputStream.getInputStream()));
    }
}