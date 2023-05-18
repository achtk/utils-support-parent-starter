package com.chua.common.support.file.transfer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * folder - jar
 *
 * @author CH
 */
public class FolderToJarFileConverter extends FolderToZipFileConverter {

    @Override
    public String target() {
        return "jar";
    }

    @Override
    protected ZipOutputStream createStream(OutputStream targetPath) throws IOException {
        Object checksum = getObject("checksum");
        if (checksum instanceof Checksum) {
            return new JarOutputStream(new BufferedOutputStream(new CheckedOutputStream(targetPath, (Checksum) checksum)));
        }
        return new JarOutputStream(targetPath);
    }

    @Override
    protected ZipEntry createFolder(String base) {
        return new ZipEntry(base);
    }

    @Override
    protected ZipEntry createFile(String base) {
        return new JarEntry(base);
    }
}
