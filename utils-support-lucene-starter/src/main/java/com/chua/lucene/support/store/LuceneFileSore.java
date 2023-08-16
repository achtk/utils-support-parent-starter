package com.chua.lucene.support.store;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.lang.store.NioFileStore;
import com.chua.common.support.lang.store.StoreConfig;
import com.chua.lucene.support.entity.DataDocument;
import com.chua.lucene.support.factory.DirectoryFactory;
import com.chua.lucene.support.operator.DocumentOperatorTemplate;
import com.chua.lucene.support.operator.IndexOperatorTemplate;
import com.chua.lucene.support.resolver.LuceneTemplateResolver;

import java.time.LocalDate;
import java.util.UUID;

/**
 * lucene存储
 *
 * @author CH
 */
@Spi("lucene")
public class LuceneFileSore extends NioFileStore {

    private final LuceneTemplateResolver luceneTemplateResolver;
    private IndexOperatorTemplate indexOperatorTemplate;
    final int fragmentation = Runtime.getRuntime().availableProcessors() - 1;

    public LuceneFileSore(String path, String suffix, StoreConfig storeConfig) {
        super(path, suffix, storeConfig);
        this.luceneTemplateResolver = new LuceneTemplateResolver(file.toPath(), DirectoryFactory.DirectoryType.NIO);
        this.indexOperatorTemplate = luceneTemplateResolver.getIndexOperatorTemplate();

    }

    @Override
    public void write(String applicationName, String message, String parent) {
        String index = FORMATTER.format(LocalDate.now());
        index = applicationName + index;
        checkIndex(index);
        try {
            addDocument(index, applicationName, message, parent);
        } catch (Exception ignored) {
        }
    }

    private void addDocument(String index, String applicationName, String message, String parent) throws Exception{
        DocumentOperatorTemplate documentOperatorTemplate = luceneTemplateResolver.getDocumentOperatorTemplate(index);
        DataDocument document = new DataDocument();
        document.setDataId(applicationName + parent + UUID.randomUUID().toString());
        document.setData(ImmutableBuilder.builderOfStringMap()
                .put("keyword", applicationName + " " + parent)
                .put("message", message)
                .build()
        );
        documentOperatorTemplate.addDocument(document);
    }

    private void checkIndex(String index) {
        if (!indexOperatorTemplate.exist(index)) {
            try {
                indexOperatorTemplate.create(index, fragmentation);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void close() throws Exception {
        try {
            this.luceneTemplateResolver.close();
        } catch (Exception ignored) {
        }
    }


    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public void run() {
    }
}
