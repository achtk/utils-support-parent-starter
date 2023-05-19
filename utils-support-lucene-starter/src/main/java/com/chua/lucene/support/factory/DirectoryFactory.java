package com.chua.lucene.support.factory;

import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Directory工厂
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/3
 */
public class DirectoryFactory {

    @Getter
    private DirectoryType directoryType = DirectoryType.NIO;
    /**
     * 写索引个数
     */
    @Getter
    @Setter
    private int writeThread = 1;
    /**
     * 读索引个数
     */
    @Getter
    @Setter
    private int readThread = 1;
    @Getter
    @Setter
    private Analyzer analyzer = new StandardAnalyzer();
    private IndexWriter indexWriter;
    private DirectoryReader indexReader;

    public DirectoryFactory(DirectoryType directoryType) {
        this.directoryType = directoryType;
    }

    public DirectoryFactory(DirectoryType directoryType, int readThread) {
        this.directoryType = directoryType;
        this.readThread = readThread;
    }

    public DirectoryFactory(DirectoryType directoryType, int writeThread, int readThread) {
        this.directoryType = directoryType;
        this.readThread = readThread;
        this.writeThread = writeThread;
    }

    /**
     * 获取写取索引
     *
     * @param path Path文件
     * @return Directory
     */
    public List<IndexWriter> getIndexWriter(final Path path) throws IOException {
        Directory directory = newDirectory(path, null);
        List<IndexWriter> result = new ArrayList<>(writeThread);
        for (int i = 0; i < writeThread; i++) {
            IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
            result.add(indexWriter);
        }
        return result;
    }

    /**
     * 获取读取索引
     *
     * @param path Path文件
     * @return Directory
     */
    public List<IndexReader> getIndexReader(final Path path) throws IOException {
        Directory directory = newDirectory(path, null);
        List<IndexReader> result = new ArrayList<>(readThread);
        for (int i = 0; i < readThread; i++) {
            IndexReader indexReader = DirectoryReader.open(directory);
            result.add(indexReader);
        }
        return result;
    }

    /**
     * 获取目录
     *
     * @param path Path文件
     * @return Directory
     */
    public Directory newDirectory(final Path path) throws IOException {
        return newDirectory(path, null);
    }

    /**
     * 获取目录
     *
     * @param path        Path文件
     * @param lockFactory 锁机制
     * @return Directory
     */
    public Directory newDirectory(final Path path, final LockFactory lockFactory) throws IOException {
        if (directoryType == DirectoryType.NIO) {
            return null == lockFactory ? new NIOFSDirectory(path) : new NIOFSDirectory(path, lockFactory);
        } else if (directoryType == DirectoryType.MEM) {
            return null == path ? new RAMDirectory() : (null == lockFactory ? new RAMDirectory() : new RAMDirectory(lockFactory));
        } else if (directoryType == DirectoryType.MMP) {
            return null == lockFactory ? new MMapDirectory(path) : new MMapDirectory(path, lockFactory);
        } else if (directoryType == DirectoryType.SIMPLE) {
            return null == lockFactory ? new SimpleFSDirectory(path) : new SimpleFSDirectory(path, lockFactory);
        }
        return null;
    }

    public enum DirectoryType {
        /**
         * 文件工厂
         */
        NIO,
        /**
         * 内存工厂
         */
        MEM,
        /**
         * mmp
         */
        MMP,
        /**
         * simple
         */
        SIMPLE
    }
}
