package com.chua.common.support.reflection.reflections.vfs;

import com.chua.common.support.reflection.reflections.Reflections;

import java.io.IOException;
import java.util.jar.JarFile;

/**
 * an implementation of {@link BaseVfs.Dir} for {@link java.util.zip.ZipFile}
 *
 * @author Administrator
 */
public class ZipDir implements BaseVfs.Dir {
    final java.util.zip.ZipFile jarFile;

    public ZipDir(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public String getPath() {
        return jarFile != null ? jarFile.getName().replace("\\", "/") : "/NO-SUCH-DIRECTORY/";
    }

    @Override
    public Iterable<BaseVfs.VfsFile> getFiles() {
        return () -> jarFile.stream()
                .filter(entry -> !entry.isDirectory())
                .map(entry -> (BaseVfs.VfsFile) new ZipFile(ZipDir.this, entry))
                .iterator();
    }

    @Override
    public void close() {
        try {
            jarFile.close();
        } catch (IOException e) {
            if (Reflections.log != null) {
                Reflections.log.warn("Could not close JarFile", e);
            }
        }
    }

    @Override
    public String toString() {
        return jarFile.getName();
    }
}
