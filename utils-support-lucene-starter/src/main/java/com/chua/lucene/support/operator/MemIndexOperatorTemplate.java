package com.chua.lucene.support.operator;

import com.chua.common.support.crypto.Encrypt;
import com.chua.lucene.support.factory.DirectoryFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.Collections;
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
public class MemIndexOperatorTemplate implements IndexOperatorTemplate {

    private Directory directory;
    private final Encrypt encrypt;
    private final DirectoryFactory directoryFactory;

    public MemIndexOperatorTemplate(Encrypt encrypt, DirectoryFactory directoryFactory) {
        this.encrypt = encrypt;
        this.directoryFactory = directoryFactory;
        try {
            this.directory = directoryFactory.newDirectory(null);
        } catch (IOException ignored) {
        }
    }


    @Override
    public void create(String name, int fragmentation) throws Exception {
    }

    @Override
    public void delete(String name) throws Exception {
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
        return false;
    }

    @Override
    public List<IndexWriter> indexWrite(String index) {
        try {
            return Collections.singletonList(new IndexWriter(directory, new IndexWriterConfig(getAnalyzer())));
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<IndexReader> indexReader(String index) {
        try {
            return Collections.singletonList(DirectoryReader.open(directory));
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Analyzer getAnalyzer() {
        return directoryFactory.getAnalyzer();
    }

    @Override
    public Set<String> getCollections() {
        return new HashSet<>();
    }

    @Override
    public void close() throws Exception {
        directory.close();
    }
}
