package com.chua.common.support.reflection.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

/**
 * an implementation of {@link Vfs.File} for {@link ZipEntry}
 *
 * @author Administrator
 */
public class ZipFile implements Vfs.File {
    private final ZipDir root;
    private final ZipEntry entry;

    public ZipFile(final ZipDir root, ZipEntry entry) {
        this.root = root;
        this.entry = entry;
    }

    @Override
    public String getName() {
        String name = entry.getName();
        return name.substring(name.lastIndexOf("/") + 1);
    }

    @Override
    public String getRelativePath() {
        return entry.getName();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return root.jarFile.getInputStream(entry);
    }

    @Override
    public String toString() {
        return root.getPath() + "!" + java.io.File.separatorChar + entry.toString();
    }
}
