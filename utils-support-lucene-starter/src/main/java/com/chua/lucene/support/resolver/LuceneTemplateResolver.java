package com.chua.lucene.support.resolver;

import com.chua.common.support.crypto.Encrypt;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.lucene.support.factory.DirectoryFactory;
import com.chua.lucene.support.operator.*;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;



/**
 * lucene模板
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/3
 */
public class LuceneTemplateResolver implements AutoCloseable {
    /**
     * desede
     */
    private static final String DESEDE = "des";
    /**
     * 存储位置
     */
    @NonNull
    private Path storePath = Paths.get(System.getProperty("user.home"), "lucene-data");
    /**
     * 加解密
     */
    @NonNull
    private Encrypt encrypt = ServiceProvider.of(Encrypt.class).getExtension(DESEDE);
    /**
     * 索引模板
     */
    private IndexOperatorTemplate indexOperatorTemplate;
    /**
     * 秘钥
     */
    private String secret = "lucene";
    /**
     * 目录类型
     */
    private DirectoryFactory.DirectoryType directoryType = DirectoryFactory.DirectoryType.MEM;
    /**
     * Directory工厂
     */
    private DirectoryFactory directoryFactory = new DirectoryFactory(this.directoryType);

    public LuceneTemplateResolver() {
        this.encrypt.accessKey(this.secret);
        if (directoryType == DirectoryFactory.DirectoryType.MEM) {
            this.indexOperatorTemplate = new MemIndexOperatorTemplate(encrypt, directoryFactory);
        } else {
            this.indexOperatorTemplate = new DefaultIndexOperatorTemplate(this.storePath, this.encrypt, directoryFactory);
        }
    }

    /**
     * 构造
     *
     * @param storePath 存储位置
     * @param encrypt   加密算法
     */
    public LuceneTemplateResolver(@NonNull Path storePath, @NonNull Encrypt encrypt) {
        this.storePath = storePath;
        this.encrypt = encrypt;
        this.encrypt.accessKey(this.secret);
        if (directoryType == DirectoryFactory.DirectoryType.MEM) {
            this.indexOperatorTemplate = new MemIndexOperatorTemplate(this.encrypt, directoryFactory);
        } else {
            this.indexOperatorTemplate = new DefaultIndexOperatorTemplate(this.storePath, this.encrypt, directoryFactory);
        }
    }

    /**
     * 构造
     *
     * @param storePath 存储位置
     * @param encrypt   加密算法
     * @param secret    秘钥
     */
    public LuceneTemplateResolver(@NonNull Path storePath, @NonNull Encrypt encrypt, String secret) {
        this.storePath = storePath;
        this.encrypt = encrypt;
        this.secret = secret;
        this.encrypt.accessKey(this.secret);
        if (directoryType == DirectoryFactory.DirectoryType.MEM) {
            this.indexOperatorTemplate = new MemIndexOperatorTemplate(this.encrypt, directoryFactory);
        } else {
            this.indexOperatorTemplate = new DefaultIndexOperatorTemplate(this.storePath, this.encrypt, directoryFactory);
        }
    }

    /**
     * 构造
     *
     * @param directoryType 目录类型
     */
    public LuceneTemplateResolver(DirectoryFactory.DirectoryType directoryType) {
        this.encrypt.accessKey( this.secret);
        this.directoryType = directoryType;
        this.directoryFactory = new DirectoryFactory(this.directoryType);
        if (directoryType == DirectoryFactory.DirectoryType.MEM) {
            this.indexOperatorTemplate = new MemIndexOperatorTemplate(this.encrypt, directoryFactory);
        } else {
            this.indexOperatorTemplate = new DefaultIndexOperatorTemplate(this.storePath, this.encrypt, directoryFactory);
        }

    }

    /**
     * 构造
     *
     * @param storePath     存储位置
     * @param directoryType 目录类型
     */
    public LuceneTemplateResolver(@NonNull Path storePath, DirectoryFactory.DirectoryType directoryType) {
        this.storePath = storePath;
        this.encrypt.accessKey(this.secret);
        this.directoryType = directoryType;
        this.directoryFactory = new DirectoryFactory(this.directoryType);
        if (directoryType == DirectoryFactory.DirectoryType.MEM) {
            this.indexOperatorTemplate = new MemIndexOperatorTemplate(this.encrypt, directoryFactory);
        } else {
            this.indexOperatorTemplate = new DefaultIndexOperatorTemplate(this.storePath, this.encrypt, directoryFactory);
        }
    }

    /**
     * 构造
     *
     * @param storePath     存储位置
     * @param encrypt       加密算法
     * @param secret        秘钥
     * @param directoryType 目录类型
     */
    public LuceneTemplateResolver(@NonNull Path storePath, @NonNull Encrypt encrypt, String secret, DirectoryFactory.DirectoryType directoryType) {
        this.storePath = storePath;
        this.encrypt = encrypt;
        this.secret = secret;
        this.encrypt.accessKey( this.secret);
        this.directoryType = directoryType;
        this.directoryFactory = new DirectoryFactory(this.directoryType);
        if (directoryType == DirectoryFactory.DirectoryType.MEM) {
            this.indexOperatorTemplate = new MemIndexOperatorTemplate(this.encrypt, directoryFactory);
        } else {
            this.indexOperatorTemplate = new DefaultIndexOperatorTemplate(this.storePath, this.encrypt, directoryFactory);
        }
    }

    /**
     * 构造
     *
     * @param storePath        存储位置
     * @param encrypt          加密算法
     * @param secret           秘钥
     * @param directoryFactory 目录类型
     */
    public LuceneTemplateResolver(@NonNull Path storePath, @NonNull Encrypt encrypt, String secret, DirectoryFactory directoryFactory) {
        this.storePath = storePath;
        this.encrypt = encrypt;
        this.secret = secret;
        this.encrypt.accessKey(this.secret);
        this.directoryType = directoryFactory.getDirectoryType();
        this.directoryFactory = directoryFactory;
        if (directoryType == DirectoryFactory.DirectoryType.MEM) {
            this.indexOperatorTemplate = new MemIndexOperatorTemplate(this.encrypt, directoryFactory);
        } else {
            this.indexOperatorTemplate = new DefaultIndexOperatorTemplate(this.storePath, this.encrypt, directoryFactory);
        }
    }

    /**
     * 获取索引模板
     *
     * @return IndexOperatorTemplate
     */
    public IndexOperatorTemplate getIndexOperatorTemplate() {
        return indexOperatorTemplate;
    }

    /**
     * 获取文档模板
     *
     * @return IndexOperatorTemplate
     */
    public DocumentOperatorTemplate getDocumentOperatorTemplate(String index) throws IOException {
        if (directoryType == DirectoryFactory.DirectoryType.MEM) {
            return new MemDocumentOperatorTemplate(indexOperatorTemplate, index);
        } else {
            return new DefaultDocumentOperatorTemplate(indexOperatorTemplate, index);
        }
    }

    /**
     * 获取查询模板
     *
     * @return IndexOperatorTemplate
     */
    public SearchOperatorTemplate getSearchOperatorTemplate(String index) throws IOException {
        if (directoryType == DirectoryFactory.DirectoryType.MEM) {
            return new MemSearchOperatorTemplate(indexOperatorTemplate, index);
        } else {
            return new DefaultSearchOperatorTemplate(indexOperatorTemplate, index);
        }
    }

    /**
     */
    @Override
    public void close() throws Exception {
        indexOperatorTemplate.close();
    }

    @Override
    public LuceneTemplateResolver clone() {
        return new LuceneTemplateResolver(storePath, encrypt, secret, directoryFactory);
    }
}
