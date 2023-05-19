package com.chua.apache.support.io;


import com.chua.common.support.io.inner.AbstractFileInnerCompressInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * apache
 *
 * @author CH
 */
public abstract class AbstractApacheInnerCompressInputStream extends AbstractFileInnerCompressInputStream {

    /**
     * 流
     *
     * @param inputStream 流
     * @return 流
     * @throws IOException ex
     */
    protected InputStream createStream(ArchiveInputStream inputStream) throws IOException {
        ArchiveEntry tae = null;
        try (ArchiveInputStream archiveInputStream = inputStream) {
            while ((tae = archiveInputStream.getNextEntry()) != null) {
                InputStream stream = findStream(tae, archiveInputStream);
                if (null != stream) {
                    return stream;
                }
            }
        }
        return null;
    }

    /**
     * 获取流
     *
     * @param tae                item
     * @param archiveInputStream 流
     * @return 数据
     */
    protected InputStream findStream(ArchiveEntry tae, ArchiveInputStream archiveInputStream) {
        if (tae.getName().equals(file)) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                int count;
                byte[] data = new byte[2048];
                while ((count = archiveInputStream.read(data)) != -1) {
                    bos.write(data, 0, count);
                }
                return new ByteArrayInputStream(bos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取流
     *
     * @return 流
     * @throws IOException ex
     */
    abstract ArchiveInputStream createArchiveInputStream() throws IOException;

    @Override
    protected InputStream createStream() throws IOException {
        return createStream(createArchiveInputStream());
    }
}
