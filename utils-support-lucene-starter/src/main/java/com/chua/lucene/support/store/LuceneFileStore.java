package com.chua.lucene.support.store;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.crypto.NoneCodec;
import com.chua.common.support.lang.store.NioFileStore;
import com.chua.common.support.lang.store.StoreConfig;
import com.chua.lucene.support.entity.DataDocument;
import com.chua.lucene.support.entity.HitData;
import com.chua.lucene.support.factory.DirectoryFactory;
import com.chua.lucene.support.operator.DocumentOperatorTemplate;
import com.chua.lucene.support.operator.IndexOperatorTemplate;
import com.chua.lucene.support.operator.SearchOperatorTemplate;
import com.chua.lucene.support.resolver.LuceneTemplateResolver;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * lucene存储
 *
 * @author CH
 */
@Spi(value = {"lucene", "nio"}, order = -1)
public class LuceneFileStore extends NioFileStore {

    private final LuceneTemplateResolver luceneTemplateResolver;
    private IndexOperatorTemplate indexOperatorTemplate;
    final int fragmentation = Runtime.getRuntime().availableProcessors() - 1;

    public LuceneFileStore(String path, String suffix, StoreConfig storeConfig) {
        super(path, suffix, storeConfig);
        this.luceneTemplateResolver = new LuceneTemplateResolver(file.toPath(),
                new NoneCodec(),
                null,
                DirectoryFactory.DirectoryType.NIO);
        this.indexOperatorTemplate = luceneTemplateResolver.getIndexOperatorTemplate();

    }

    @Override
    public void write(String applicationName, String message, String parent) {
        String index = FORMATTER.format(LocalDate.now());
        //index = applicationName + index;
        runExecutor.execute(() -> {
            checkIndex(index);
            try {
                addDocument(index, applicationName, message, parent);
            } catch (Exception ignored) {
            }
        });
    }

    private void addDocument(String index, String applicationName, String message, String parent) throws Exception{
        DocumentOperatorTemplate documentOperatorTemplate = luceneTemplateResolver.getDocumentOperatorTemplate(index);
        DataDocument document = new DataDocument();
        document.setDataId(applicationName + parent + UUID.randomUUID().toString());
        document.setData(ImmutableBuilder.builderOfStringMap()
                .put("keyword", applicationName + " " + parent)
                .put("message", message)
                .put("applicationName", applicationName)
                .put("mode", parent)
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
        super.close();
        try {
            this.luceneTemplateResolver.close();
        } catch (Exception ignored) {
        }
    }


    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public List<Map<String, Object>> search(String keyword) {
        List<Map<String, Object>> rs = new LinkedList<>();
        LocalDate day = LocalDate.now().minusDays(3);
        while (!day.isAfter(LocalDate.now())) {
            SearchOperatorTemplate searchOperatorTemplate = null;
            try {
                searchOperatorTemplate = this.luceneTemplateResolver.getSearchOperatorTemplate(FORMATTER.format(day));
            } catch (IOException ignored) {
            }
            if(null == searchOperatorTemplate) {
                continue;
            }
            HitData hitData = searchOperatorTemplate.search(keyword);
            rs.addAll(hitData.getData());
            day = day.plusDays(1);
        }
        return rs;
    }
}
