package com.chua.common.support.io.inner;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.xz.XZInputStream;

import java.io.*;
import java.nio.file.Files;

/**
 * xz流
 *
 * @author CH
 */
@Spi("xz")
public class XzFileInnerCompressInputStream extends AbstractFileInnerCompressInputStream {

    @Override
    protected InputStream createStream() throws IOException {
        try (InputStream in = new XZInputStream(getFileInputStream())) {
            int count;
            byte[] data = new byte[2048];
            try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
                while ((count = in.read(data)) != -1) {
                    fos.write(data, 0, count);
                }
                fos.flush();
                return new ByteArrayInputStream(fos.toByteArray());
            } catch (Exception ignored) {
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
