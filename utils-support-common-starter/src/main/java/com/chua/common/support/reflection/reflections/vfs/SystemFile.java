package com.chua.common.support.reflection.reflections.vfs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * an implementation of {@link BaseVfs.VfsFile} for a directory {@link java.io.File}
 *
 * @author Administrator
 */
public class SystemFile implements BaseVfs.VfsFile {
    private final SystemDir root;
    private final java.io.File file;

    public SystemFile(final SystemDir root, java.io.File file) {
        this.root = root;
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getRelativePath() {
        String filepath = file.getPath().replace("\\", "/");
        if (filepath.startsWith(root.getPath())) {
            return filepath.substring(root.getPath().length() + 1);
        }

        return null;
    }

    @Override
    public InputStream openInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
