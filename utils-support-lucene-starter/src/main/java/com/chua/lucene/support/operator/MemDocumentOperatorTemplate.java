package com.chua.lucene.support.operator;

import com.chua.common.support.utils.CollectionUtils;
import com.chua.lucene.support.entity.DataDocument;
import com.chua.lucene.support.util.DocumentUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认的文档操作模板
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/3
 */
@Slf4j
@AllArgsConstructor
public class MemDocumentOperatorTemplate implements DocumentOperatorTemplate {

    /**
     * 索引模板
     */
    private final IndexOperatorTemplate indexOperatorTemplate;
    /**
     * 索引
     */
    private final String index;

    @Override
    public void addDocument(DataDocument dataDocument) throws Exception {
        List<IndexWriter> indexWriterList = indexOperatorTemplate.indexWrite(index);
        try (IndexWriter indexWriter = CollectionUtils.getRandom(indexWriterList)) {
            indexWriter.addDocument(DocumentUtil.map2Document(dataDocument));
            indexWriter.commit();
        }
    }

    @Override
    public void addDocuments(List<DataDocument> dataDocument) throws Exception {
        long startTime = 0L;
        if (log.isDebugEnabled()) {
            startTime = System.currentTimeMillis();
            log.debug("开始处理数据，数量:{}", dataDocument.size());
        }
        List<IndexWriter> indexWriterList = indexOperatorTemplate.indexWrite(index);
        int size = indexWriterList.size();
        AtomicInteger atomic = new AtomicInteger();
        dataDocument.parallelStream().forEach(dataDocument1 -> {
            IndexWriter indexWriter = indexWriterList.get(atomic.get() % size);
            try {
                indexWriter.addDocument(DocumentUtil.map2Document(dataDocument.get(atomic.getAndIncrement())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        for (IndexWriter indexWriter : indexWriterList) {
            indexWriter.commit();
            indexWriter.close();
        }
        if (log.isDebugEnabled()) {
            log.debug("处理数据完成, 处理数量:{}, 耗时: {}ms", dataDocument.size(), System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public void deleteDocument(String dataId) throws Exception {
        List<IndexWriter> indexWriterList = indexOperatorTemplate.indexWrite(index);
        for (IndexWriter indexWriter : indexWriterList) {
            indexWriter.deleteDocuments(new Term("dataId:" + dataId));
            indexWriter.commit();
            indexWriter.close();
        }
    }

    @Override
    public void updateDocument(DataDocument dataDocument) throws Exception {
        List<IndexWriter> indexWriterList = indexOperatorTemplate.indexWrite(index);
        try (IndexWriter indexWriter = CollectionUtils.getRandom(indexWriterList)) {
            indexWriter.updateDocument(new Term("dataId:" + dataDocument.getDataId()), DocumentUtil.map2Document(dataDocument));
            indexWriter.commit();
        }
    }

    @Override
    public void updateDocuments(List<DataDocument> dataDocument) throws Exception {
        List<IndexWriter> indexWriterList = indexOperatorTemplate.indexWrite(index);
        for (IndexWriter indexWriter : indexWriterList) {
            for (DataDocument document : dataDocument) {
                indexWriter.updateDocument(new Term("dataId:" + document.getDataId()), DocumentUtil.map2Document(document));
            }
        }
        for (IndexWriter indexWriter : indexWriterList) {
            indexWriter.commit();
            indexWriter.close();
        }
    }
}
