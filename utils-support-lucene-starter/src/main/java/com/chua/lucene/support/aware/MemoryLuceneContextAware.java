package com.chua.lucene.support.aware;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 内存模式启动lucene
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
public class MemoryLuceneContextAware extends NioFSLuceneContextAware {

    public MemoryLuceneContextAware() throws IOException {
        super(null, new RAMDirectory());
    }

    public MemoryLuceneContextAware(Path path, Directory directory) throws IOException {
        super(path, directory);
    }

    public static MemoryLuceneContextAware newNoLockFactory() throws IOException {
        return new MemoryLuceneContextAware(null, new RAMDirectory(NoLockFactory.INSTANCE));
    }
}
