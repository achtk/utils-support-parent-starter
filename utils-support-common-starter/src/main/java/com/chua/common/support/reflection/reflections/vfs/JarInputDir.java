package com.chua.common.support.reflection.reflections.vfs;

import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.ReflectionsException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 * @author Administrator
 */
public class JarInputDir implements Vfs.Dir {
    private final URL url;
    JarInputStream jarInputStream;
    long cursor = 0;
    long nextCursor = 0;

    public JarInputDir(URL url) {
        this.url = url;
    }

    @Override
    public String getPath() {
        return url.getPath();
    }

    @Override
    public Iterable<Vfs.VfsFile> getFiles() {
        return () -> new Iterator<Vfs.VfsFile>() {
            {
                try {
                    jarInputStream = new JarInputStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    throw new ReflectionsException("Could not open url connection", e);
                }
            }

            Vfs.VfsFile entry = null;

            @Override
            public boolean hasNext() {
                return entry != null || (entry = computeNext()) != null;
            }

            @Override
            public Vfs.VfsFile next() {
                Vfs.VfsFile next = entry;
                entry = null;
                return next;
            }

            private Vfs.VfsFile computeNext() {
                while (true) {
                    try {
                        ZipEntry entry = jarInputStream.getNextJarEntry();
                        if (entry == null) {
                            return null;
                        }

                        long size = entry.getSize();
                        if (size < 0) {
                            size = 0xffffffffL + size; //JDK-6916399
                        }
                        nextCursor += size;
                        if (!entry.isDirectory()) {
                            return new JarInputFile(entry, JarInputDir.this, cursor, nextCursor);
                        }
                    } catch (IOException e) {
                        throw new ReflectionsException("could not get next zip entry", e);
                    }
                }
            }
        };
    }

    @Override
    public void close() {
        try {
            if (jarInputStream != null) {
                ((InputStream) jarInputStream).close();
            }
        } catch (IOException e) {
            if (Reflections.log != null) {
                Reflections.log.warn("Could not close InputStream", e);
            }
        }
    }
}
