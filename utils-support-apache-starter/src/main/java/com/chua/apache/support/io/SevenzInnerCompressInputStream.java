package com.chua.apache.support.io;

import com.chua.common.support.annotations.SpiCondition;
import com.chua.common.support.io.inner.AbstractFileInnerCompressInputStream;
import com.chua.common.support.matcher.AntPathMatcher;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.*;

/**
 * 7z
 *
 * @author CH
 */
@SpiCondition("org.apache.commons.compress.archivers.ArchiveEntry")
public class SevenzInnerCompressInputStream extends AbstractFileInnerCompressInputStream {

    @Override
    protected InputStream createStream() throws IOException {
        SevenZFile zFile = new SevenZFile(new File(parent, file));
        SevenZArchiveEntry nextEntry = null;
        while ((nextEntry = zFile.getNextEntry()) != null) {
            if (AntPathMatcher.INSTANCE.match(file, nextEntry.getName())) {
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    int count;
                    byte[] data = new byte[2048];
                    while ((count = zFile.read(data)) != -1) {
                        bos.write(data, 0, count);
                    }
                    return new ByteArrayInputStream(bos.toByteArray());
                }
            }
        }
        return null;
    }
}
