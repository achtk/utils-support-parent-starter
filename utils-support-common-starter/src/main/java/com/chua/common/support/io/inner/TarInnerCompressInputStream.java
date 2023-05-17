package com.chua.common.support.io.inner;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.tar.TarEntry;
import com.chua.common.support.file.tar.TarInputStream;

import java.io.*;
import java.nio.file.Files;

/**
 * tar流
 *
 * @author CH
 */
@Spi("tar")
public class TarInnerCompressInputStream extends AbstractInnerCompressInputStream {

    @Override
    protected InputStream createStream() throws IOException {
        try (TarInputStream tis = new TarInputStream(getFileInputStream())) {
            TarEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                if (entry.getName().endsWith(file)) {
                    int count;
                    byte[] data = new byte[2048];
                    try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
                        while ((count = tis.read(data)) != -1) {
                            fos.write(data, 0, count);
                        }
                        fos.flush();
                        return new ByteArrayInputStream(fos.toByteArray());
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    protected InputStream getFileInputStream() throws IOException {
        return Files.newInputStream(new File(parent).toPath());
    }
}
