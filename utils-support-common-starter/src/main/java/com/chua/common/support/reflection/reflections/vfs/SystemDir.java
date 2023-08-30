package com.chua.common.support.reflection.reflections.vfs;

import com.chua.common.support.reflection.reflections.ReflectionsException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

/**
 * An implementation of {@link Vfs.Dir} for directory {@link File}.
 *
 * @author Administrator
 */
public class SystemDir implements Vfs.Dir {
    private final File file;

    public SystemDir(File file) {
        boolean b = file != null && (!file.isDirectory() || !file.canRead());
        if (b) {
            throw new RuntimeException("cannot use dir " + file);
        }
        this.file = file;
    }

    @Override
    public String getPath() {
        return file != null ? file.getPath().replace("\\", "/") : "/NO-SUCH-DIRECTORY/";
    }

    @Override
    public Iterable<Vfs.VfsFile> getFiles() {
        if (file == null || !file.exists()) {
            return Collections.emptyList();
        }
        return () -> {
            try {
                return Files.walk(file.toPath())
                        .filter(Files::isRegularFile)
                        .map(path -> (Vfs.VfsFile) new SystemFile(SystemDir.this, path.toFile()))
                        .iterator();
            } catch (IOException e) {
                throw new ReflectionsException("could not get files for " + file, e);
            }
        };
    }
}
