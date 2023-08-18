package com.chua.lucene.support.operator;

import com.chua.common.support.crypto.Codec;
import com.chua.lucene.support.factory.DirectoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 默认索引操作模板
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/3
 */
@Slf4j
public class DefaultIndexOperatorTemplate implements IndexOperatorTemplate {

    private final Path path;
    private final Codec encrypt;
    private final DirectoryFactory directoryFactory;

    public DefaultIndexOperatorTemplate(Path path, Codec encrypt, DirectoryFactory directoryFactory) {
        this.path = path;
        this.encrypt = encrypt;
        this.directoryFactory = directoryFactory;
    }


    @Override
    public void create(String name, int fragmentation) throws Exception {
        //真实存储的索引
        if (exist(name)) {
            throw new IllegalStateException("Index already exists");
        }
        //索引分片
        fragmentation = fragmentation < 1 ? 1 : fragmentation;
        for (int i = 0; i < fragmentation; i++) {
            //分片索引
            Path fragPath = Paths.get(path.toAbsolutePath().toString(), safeStoreIndexName(name), i + "");
            //创建索引
            try (Directory directory = directoryFactory.newDirectory(fragPath)) {
            }
        }
    }

    @Override
    public void delete(String name) throws Exception {
        if (exist(name)) {
            throw new IllegalStateException("Index does not exist");
        }
        Path path = Paths.get(this.path.toAbsolutePath().toString(), safeStoreIndexName(name));
        path.toFile().deleteOnExit();
    }

    @Override
    public String getStoreIndexName(String index) throws Exception {
        return encrypt.encodeHex(index);
    }

    @Override
    public String getRealIndexName(String index) throws Exception {
        return encrypt.decodeHex(index);
    }

    @Override
    public boolean exist(String index) {
        //真实存储的索引
        Path newPath = Paths.get(path.toAbsolutePath().toString(), safeStoreIndexName(index));
        if (newPath.toFile().exists()) {
            return true;
        }
        return false;
    }

    @Override
    public List<IndexWriter> indexWrite(String index) {
        List<IndexWriter> indexWriterList = new ArrayList<>();

        Path path = Paths.get(this.path.toAbsolutePath().toString(), safeStoreIndexName(index));
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (path == dir) {
                        return FileVisitResult.CONTINUE;
                    }
                    List<IndexWriter> indexWriter = null;
                    try {
                        indexWriter = directoryFactory.getIndexWriter(dir);
                    } catch (IOException ignored) {
                    }
                    if(null == indexWriter) {
                        return FileVisitResult.CONTINUE;
                    }
                    indexWriterList.addAll(indexWriter);
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexWriterList;
    }

    @Override
    public List<IndexReader> indexReader(String index) {
        List<IndexReader> indexReaderList = new ArrayList<>();

        Path path = Paths.get(this.path.toAbsolutePath().toString(), safeStoreIndexName(index));
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (path == dir) {
                        return FileVisitResult.CONTINUE;
                    }
                    List<IndexReader> indexReaders = null;
                    try {
                        indexReaders = directoryFactory.getIndexReader(dir);
                    } catch (IOException ignored) {
                    }
                    try {
                        indexReaderList.addAll(indexReaders);
                    } catch (Exception e) {
                        log.warn("Index {} data does not exist!!", dir.getFileName());
                    }
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexReaderList;
    }

    @Override
    public Analyzer getAnalyzer() {
        return directoryFactory.getAnalyzer();
    }

    @Override
    public Set<String> getCollections() {
        Set<String> result = new HashSet<>();
        Path path = Paths.get(this.path.toAbsolutePath().toString());
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (path == dir) {
                        return FileVisitResult.CONTINUE;
                    }
                    String name = dir.getFileName().toString();
                    if (null != encrypt) {
                        try {
                            name = encrypt.decodeHex(name);
                        } catch (Exception ignored) {
                        }
                    }
                    result.add(name);
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void close() throws Exception {
    }
}
