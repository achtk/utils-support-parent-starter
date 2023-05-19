package com.chua.lucene.support.aware;

import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * MMap
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/2
 */
public class MMapLuceneContextAware extends NioFSLuceneContextAware {

    public MMapLuceneContextAware(Path path) throws IOException {
        super(path, new MMapDirectory(path));
    }
}
