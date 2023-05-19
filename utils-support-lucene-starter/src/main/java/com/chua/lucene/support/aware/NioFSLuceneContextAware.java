package com.chua.lucene.support.aware;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.lucene.support.entity.Search;
import com.chua.lucene.support.util.DocumentUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * lucene
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/10/30
 */
@Slf4j
public class NioFSLuceneContextAware {

    @Getter
    @Setter
    private Path path;
    private Directory directory;
    private Analyzer analyzer;
    private IndexWriter indexWriter;
    private DirectoryReader indexReader;

    public NioFSLuceneContextAware() {
    }

    public NioFSLuceneContextAware(Path path) throws IOException {
        this(path, new StandardAnalyzer(), new NIOFSDirectory(path));
    }

    public NioFSLuceneContextAware(Path path, Analyzer analyzer) throws IOException {
        this(path, analyzer, new NIOFSDirectory(path));
    }

    public NioFSLuceneContextAware(Path path, Directory directory) throws IOException {
        this(path, new StandardAnalyzer(), directory);
    }

    public NioFSLuceneContextAware(Path path, Analyzer analyzer, Directory directory) throws IOException {
        this.path = path;
        this.analyzer = analyzer;
        this.directory = directory;
        // 分词器
        this.indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
        if (DirectoryReader.indexExists(directory)) {
            this.indexReader = DirectoryReader.open(directory);
        }
    }

    public void updateDocument(Map<String, Object> documentMap) throws Exception {
        Document document = DocumentUtil.map2Document(documentMap);
        indexWriter.updateDocument(new Term(DocumentUtil.ID, document.get(DocumentUtil.ID)), document);
        indexWriter.commit();
    }

    public void updateDocuments(List<Map<String, Object>> documentMaps) throws Exception {
        for (Map<String, Object> document : documentMaps) {
            updateDocument(document);
        }
    }

    public void addDocument(Map<String, Object> documentMap) throws Exception {
        Document document = DocumentUtil.map2Document(documentMap);
        indexWriter.addDocument(document);
        indexWriter.commit();
    }

    public void addDocuments(List<Map<String, Object>> documentMaps) throws Exception {
        List<Document> documents = DocumentUtil.map2Documents(documentMaps);
        indexWriter.addDocuments(documents);
        indexWriter.commit();
    }

    public long deleteDocument(String expression) throws Exception {
        StandardQueryParser queryParser = new StandardQueryParser(analyzer);
        // 支持后缀匹配，如*国 则可以搜索中国、美国等以国字结尾的词，*:*可以查询所有索引
        queryParser.setAllowLeadingWildcard(true);
        // 2 使用查询解析器对象, 实例化Query对象
        Query query = queryParser.parse(expression, DocumentUtil.CREATE_TIME);
        return indexWriter.deleteDocuments(query);
    }

    public Map<String, Object> search(Search search) throws Exception {
        List<String> fields = search.getFields();
        Search.Match searchMatch = search.getMatch();
        if (searchMatch == Search.Match.FIELD && CollectionUtils.isEmpty(fields)) {
            return null;
        }
        //初始化读取器
        checkIndexReader();
        // 1 创建查询解析器对象
        // 参数一:默认的搜索域, 参数二:使用的分析器
        StandardQueryParser queryParser = new StandardQueryParser(analyzer);
        // 支持后缀匹配，如*国 则可以搜索中国、美国等以国字结尾的词，*:*可以查询所有索引
        queryParser.setAllowLeadingWildcard(true);
        // 2 使用查询解析器对象, 实例化Query对象
        Query query = queryParser.parse(search.getSearch(), DocumentUtil.CREATE_TIME);
        return queryForDocument(query, search);
    }

    /**
     * 初始化读取器
     */
    private void checkIndexReader() throws IOException {
        if (indexReader == null) {
            synchronized (this) {
                if (!DirectoryReader.indexExists(directory)) {
                    log.warn("索引数据不存在无法查询");
                    throw new IOException();
                }
                this.indexReader = DirectoryReader.open(directory);
            }
        }
    }

    /**
     * 查询数据
     *
     * @param query  查询对象
     * @param search 查询条件
     * @return
     */
    private Map<String, Object> queryForDocument(Query query, Search search) throws IOException {
        List<String> fields = search.getFields();
        Search.Match searchMatch = search.getMatch();
        // 3. 创建索引搜索对象(IndexSearcher), 用于执行索引
        IndexSearcher searcher = new IndexSearcher(indexReader);
        // 4. 使用IndexSearcher对象执行搜索, 返回搜索结果集TopDocs
        // 参数一:使用的查询对象, 参数二:指定要返回的搜索结果排序后的前n个
        TopDocs topDocs = searcher.search(query, search.getMax());

        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> documentData = new HashMap<>();

        documentData.put("hit", topDocs.totalHits.value);
        documentData.put("data", data);

        Arrays.stream(topDocs.scoreDocs).forEach(scoreDoc -> {
            Document document = null;
            try {
                document = searcher.doc(scoreDoc.doc);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Map<String, Object> map = null;
            if (searchMatch == Search.Match.FIELD) {
                map = new HashMap<>(fields.size());

                for (String field : fields) {
                    String fieldValue = document.get(field);
                    map.put(field, fieldValue);
                }
            } else {
                List<IndexableField> documentFields = document.getFields();
                map = new HashMap<>(documentFields.size());
                for (IndexableField indexableField : documentFields) {
                    map.put(indexableField.name(), indexableField.stringValue());
                }
            }
            data.add(map);
        });
        return documentData;
    }

    public <T> Map<String, Object> search(Search search, Class<T> mapClass) throws Exception {
        Map<String, Object> documentData = search(search);
        if (null == documentData) {
            return null;
        }
        Map<String, Object> documentData1 = new HashMap<>();

        documentData1.put("data", Collections.emptyList());
        documentData1.put("hit", documentData.get("hit"));

        if (documentData.isEmpty()) {
            return documentData1;
        }

        List<Map<String, Object>> documentDataData = (List<Map<String, Object>>) documentData.get("data");
        T object = ClassUtils.forObject(mapClass);
        if (null == object) {
            return documentData1;
        }
        List<T> newData = new ArrayList<>(documentDataData.size());
        for (Map<String, Object> dataDatum : documentDataData) {
            T object1 = ClassUtils.forObject(mapClass);

            BeanUtils.copyProperties(dataDatum, object1);
            newData.add(object1);
        }
        documentData1.put("data", newData);
        return documentData1;
    }

    public Map<String, Object> searchKeyword(Search search) throws Exception {
        //多字段的查询转换器
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(search.getFields().toArray(new String[0]), analyzer);
        List<String> fields = search.getFields();
        Search.Match searchMatch = search.getMatch();
        if (searchMatch == Search.Match.FIELD && CollectionUtils.isEmpty(fields)) {
            return null;
        }
        //初始化读取器
        checkIndexReader();

        return queryForDocument(queryParser.parse(search.getSearch()), search);
    }

    public void deleteAll() throws Exception {
        indexWriter.deleteAll();
    }

    public void close() throws Exception {
        indexWriter.close();
        directory.close();
    }

}
