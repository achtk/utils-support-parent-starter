package com.chua.common.support.io.inner;



import com.chua.common.support.annotations.Spi;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * jar流
 *
 * @author CH
 */
@Spi("jar")
public class JarFileInnerCompressInputStream extends AbstractFileInnerCompressInputStream {

    @Override
    protected InputStream createStream() throws IOException {
        JarFile jarFile = new JarFile(parent);
        ZipEntry jarEntry = jarFile.getEntry(file);
        return jarFile.getInputStream(jarEntry);
    }
}
