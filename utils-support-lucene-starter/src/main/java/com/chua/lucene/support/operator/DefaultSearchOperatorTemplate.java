package com.chua.lucene.support.operator;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.lucene.support.entity.DataDocument;
import com.chua.lucene.support.entity.HitData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_MINS;
import static com.chua.common.support.constant.NumberConstant.DEFAULT_INITIAL_CAPACITY;

/**
 * 查询模板
 * <p>AND | OR 关联</p>
 * <p>使用?匹配单个字符查询</p>
 * <p>使用*匹配多个字符查询</p>
 * <p>"+Licensor Wildcard"  +代表必须的条件，搜索文档必须包含Licensor 可能有Wildcard</p>
 * <p>"Licensor AND ce* AND Licenso?"  使用AND取多个关键字的并集查询</p>
 * <p>"'Lincensor Apache' NOT 'Apache Licensor'"  搜索Lincensor Apache而不是Apache Licensor</p>
 * <p>"'Lincensor Apache' - 'Apache Licensor'"  "-"同NOT的效果一样</p>
 * <p>"/[Lab]icensor/" 这个匹配Lincensor，aicensor，bicensor分词</p>
 * <p>"/[Lab]icenso[a-z]/" 根据需要可以更灵活的使用</p>
 * <p>"icensor~"  可以查到Licensor关键字，而queryParser.parse("icensor*")查不到</p>
 * <p>"Licens~1"   ~后面可加0-2的整数来制定模糊匹配度，默认不加为1</p>
 * <p>"Licens cens ~0" ~还可以模糊匹配差异化N字符数的多个关键字</p>
 * <p>"{abc TO Licens}"   {}abc与Licenszhi间的文件，不包含</p>
 * <p>"[abc TO Licens]"   []abc与Licenszhi间的文件,包含本身</p>
 * <p>Licensor Wildcard^4  默认为1，可加权可降权，可通过加权处理给匹配的结果排序</p>
 * <p>(+Licensor  +Wildcard) AND easier" 可使用（）组合多个条件查询</p>
 * <p>查询部分字符需要转义处理，如（+ - && || ! ( ) { } [ ] ^ " ~ * ? : \ /）</p>
 * @author CH
 * @version 1.0.0
 * @since 2020/11/3
 */
@Slf4j
@AllArgsConstructor
public class DefaultSearchOperatorTemplate implements SearchOperatorTemplate {

    /**
     * 索引模板
     */
    private final IndexOperatorTemplate indexOperatorTemplate;
    /**
     * 索引
     */
    private final String index;

    @Override
    public HitData search(String keyword, String sort, String columns, int offset, int pageSize) {
        if (!indexOperatorTemplate.exist(index)) {
            throw new IllegalStateException("索引不存在");
        }
        long startTime = System.currentTimeMillis();
        //获取查询器
        List<IndexReader> indexReaderList = indexOperatorTemplate.indexReader(index);

        ExecutorService executorService = ThreadUtils.newProcessorThreadExecutor(indexReaderList.size());
        final int newPageSize = (pageSize < 0 || pageSize > 1000 ? 1000 : pageSize);
        //当前页
        int endPage = offset * newPageSize;
        //当前页
        int startPage = (offset - 1) * newPageSize;
        //排序
        Sort sort1 = getSort(sort);

        final LongAdder longAdder = new LongAdder();

        List<Future<List<Map<String, Object>>>> futureList = new ArrayList<>();
        for (IndexReader indexReader : indexReaderList) {
            futureList.add(executorService.submit(() -> {
                // 1 创建查询解析器对象
                // 参数一:默认的搜索域, 参数二:使用的分析器
                StandardQueryParser queryParser = new StandardQueryParser(indexOperatorTemplate.getAnalyzer());
                // 支持后缀匹配，如*国 则可以搜索中国、美国等以国字结尾的词，*:*可以查询所有索引
                queryParser.setAllowLeadingWildcard(true);
                // 2 使用查询解析器对象, 实例化Query对象
                Query query = queryParser.parse(keyword, DataDocument.UNIQUELY_IDENTIFIES);

                // 3. 创建索引搜索对象(IndexSearcher), 用于执行索引
                IndexSearcher searcher = new IndexSearcher(indexReader);
                // 4. 使用IndexSearcher对象执行搜索, 返回搜索结果集TopDocs
                // 参数一:使用的查询对象, 参数二:前n个
                TopDocs topDocs = null;
                if (null != sort1) {
                    topDocs = searcher.search(query, endPage, sort1);
                } else {
                    topDocs = searcher.search(query, endPage);
                }
                longAdder.add(topDocs.totalHits.value);
                List<Map<String, Object>> data = new ArrayList<>();
                ScoreDoc[] scoreDocs = topDocs.scoreDocs;
                int length = scoreDocs.length;
                for (int i = 0; i < length; i++) {
                    data.add(toMap(searcher, scoreDocs[i], columns));
                }
                return data;
            }));
        }

        List<Map<String, Object>> result;
        try {
            result = getThreadPoolData(futureList);
        } finally {
            executorService.shutdownNow();
        }
        HitData hitData = new HitData();
        hitData.setData(result.size() < newPageSize ? result : result.subList(0, newPageSize));
        hitData.setHits(longAdder.longValue());
        hitData.setTotal(hitData.getData().size());
        try {
            return hitData;
        } finally {
            int size = result.size();
            if (log.isDebugEnabled()) {
                log.debug("数据查询耗时{}ms, 表达式: {}, 结果数量： {}/{}", System.currentTimeMillis() - startTime, keyword, size < pageSize ? size : pageSize, size);
            }
        }
    }

    @Override
    public HitData quickSearch(String keyword, String sort, String columns, int offset, int pageSize) {
        if (!indexOperatorTemplate.exist(index)) {
            throw new IllegalStateException("索引不存在");
        }
        long startTime = System.currentTimeMillis();
        //获取查询器
        List<IndexReader> indexReaderList = indexOperatorTemplate.indexReader(index);
        int size = indexReaderList.size();
        int thread = Math.min(size, Runtime.getRuntime().availableProcessors() * 2);
        //结果集
        final List<Map<String, Object>> data = new ArrayList<>();
        ExecutorService executorService = ThreadUtils.newFixedThreadExecutor(thread, "luence-task");
        final int newPageSize = (pageSize < 0 || pageSize > 1000 ? 1000 : pageSize);
        //当前页
        int endPage = offset * newPageSize;
        //当前页
        int startPage = (offset - 1) * newPageSize;
        //计数器
        final LongAdder longAdder = new LongAdder();
        //hit计数器
        final LongAdder hitAdder = new LongAdder();
        //排序
        Sort sort1 = getSort(sort);
        CountDownLatch countDownLatch = new CountDownLatch(thread);
        for (IndexReader indexReader : indexReaderList) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (longAdder.intValue() >= newPageSize) {
                        complete(countDownLatch);
                        return;
                    }
                    try {
                        // 1 创建查询解析器对象
                        // 参数一:默认的搜索域, 参数二:使用的分析器
                        StandardQueryParser queryParser = new StandardQueryParser(indexOperatorTemplate.getAnalyzer());
                        // 支持后缀匹配，如*国 则可以搜索中国、美国等以国字结尾的词，*:*可以查询所有索引
                        queryParser.setAllowLeadingWildcard(true);
                        // 2 使用查询解析器对象, 实例化Query对象
                        Query query = queryParser.parse(keyword, DataDocument.UNIQUELY_IDENTIFIES);

                        // 3. 创建索引搜索对象(IndexSearcher), 用于执行索引
                        IndexSearcher searcher = new IndexSearcher(indexReader);
                        // 4. 使用IndexSearcher对象执行搜索, 返回搜索结果集TopDocs
                        // 参数一:使用的查询对象, 参数二:指定要返回的搜索结果排序后的前n个
                        TopDocs topDocs = null;
                        if (null != sort1) {
                            topDocs = searcher.search(query, endPage, sort1);
                        } else {
                            topDocs = searcher.search(query, endPage);
                        }
                        if (log.isDebugEnabled()) {
                            Thread thread1 = Thread.currentThread();
                            log.debug("{}, {}#{}({})", thread1.getName(), thread1.getPriority(), keyword, topDocs.totalHits.value);
                        }
                        hitAdder.add(topDocs.totalHits.value);
                        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                            if (longAdder.intValue() >= newPageSize) {
                                complete(countDownLatch);
                                break;
                            }
                            data.add(toMap(searcher, scoreDoc, columns));
                            longAdder.increment();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }

                private void complete(CountDownLatch countDownLatch) {
                    long count = countDownLatch.getCount();
                    for (int i = 0; i < count; i++) {
                        countDownLatch.countDown();
                    }
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }
        int size1 = data.size();
        HitData hitData = new HitData();
        hitData.setData(size1 > newPageSize ? data.subList(0, newPageSize) : data);
        hitData.setHits(hitAdder.longValue());
        hitData.setTotal(longAdder.intValue());
        try {
            return hitData;
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("数据查询耗时{}ms, 表达式: {}", System.currentTimeMillis() - startTime, keyword);
            }
        }
    }

    /**
     * 获取线程池数据
     *
     * @param futureList 线程池
     * @return List
     */
    private List<Map<String, Object>> getThreadPoolData(List<Future<List<Map<String, Object>>>> futureList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Future<List<Map<String, Object>>> future : futureList) {
            List<Map<String, Object>> mapList = null;
            try {
                mapList = future.get();
            } catch (Throwable e) {
                continue;
            }
            result.addAll(mapList);
        }
        return result;
    }

    /**
     * 获取Map
     *
     * @param searcher 查询器
     * @param scoreDoc 结果
     * @param columns  字段
     * @return Map<String, Object>
     */
    private Map<String, Object> toMap(IndexSearcher searcher, ScoreDoc scoreDoc, String columns) {
        Document document = null;
        try {
            document = searcher.doc(scoreDoc.doc);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        if (null == columns || columns.isEmpty()) {
            List<IndexableField> documentFields = document.getFields();
            for (IndexableField indexableField : documentFields) {
                map.put(indexableField.name(), tryValue(indexableField));
            }
        } else {
            Document finalDocument = document;
            Splitter.on(",").trimResults().omitEmptyStrings().splitToList(columns).forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    String s1 = finalDocument.get(s);
                    map.put(s, s1);
                }
            });
        }
        return map;
    }

    private Object tryValue(IndexableField indexableField) {
        Number number = indexableField.numericValue();
        if (null != number) {
            return number;
        }
        BytesRef bytesRef = indexableField.binaryValue();
        if (null != bytesRef) {
            return bytesRef;
        }

        Reader reader = indexableField.readerValue();
        if (null != reader) {
            return reader;
        }

        CharSequence charSequenceValue = indexableField.getCharSequenceValue();
        if (null != charSequenceValue) {
            return charSequenceValue;
        }
        return indexableField.stringValue();
    }

    /**
     * 获取排序舒心
     *
     * @param sort 排序
     * @return Sort
     */
    private Sort getSort(String sort) {
        if (StringUtils.isNullOrEmpty(sort)) {
            return null;
        }
        Sort sort1 = new Sort();

        List<SortField> sortFieldList = new ArrayList<>();
        sort1.setSort(sortFieldList.toArray(new SortField[0]));
        Splitter.on(",").omitEmptyStrings().trimResults().splitToList(sort).parallelStream().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                String name = s.trim();
                if (s.startsWith(SYMBOL_MINS)) {
                    name = s.substring(1);
                }
                SortField sortField = new SortField(name, SortField.Type.LONG);
                sortFieldList.add(sortField);
            }
        });

        return sort1;
    }
}
