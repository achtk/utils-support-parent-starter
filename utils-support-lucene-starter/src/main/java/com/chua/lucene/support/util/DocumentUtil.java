package com.chua.lucene.support.util;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.utils.StringUtils;
import com.chua.lucene.support.entity.DataDocument;
import com.chua.lucene.support.field.DoubleField;
import com.chua.lucene.support.field.FloatField;
import com.chua.lucene.support.field.LongField;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * doc工具类
 *
 * @author CH
 * @version 1.0
 * @since 2020/10/20 18:12
 */
public class DocumentUtil {

    private static final Pattern PATTERN = Pattern.compile("(and|or){1}");
    public static final String CREATE_TIME = "#createTime";
    public static final String ID = "id";
    public static ConcurrentMap<String, String> javaProperty2SqlColumnMap = new ConcurrentHashMap<>();

    static {
        javaProperty2SqlColumnMap.put("Integer", "INTEGER");
        javaProperty2SqlColumnMap.put("Short", "tinyint");
        javaProperty2SqlColumnMap.put("Long", "bigint");
        javaProperty2SqlColumnMap.put("BigDecimal", "decimal(19,2)");
        javaProperty2SqlColumnMap.put("Double", "double precision not null");
        javaProperty2SqlColumnMap.put("Float", "float");
        javaProperty2SqlColumnMap.put("Boolean", "bit");
        javaProperty2SqlColumnMap.put("Timestamp", "datetime");
        javaProperty2SqlColumnMap.put("String", "VARCHAR(255)");
    }

    /**
     * 对象转Doc
     *
     * @param data Map
     * @return List
     */
    public static List<DataDocument> map2DataDocuments(List<Map<String, Object>> data) {
        return data.stream().map(item -> {
            DataDocument dataDocument = new DataDocument();
            Object o = item.get(DataDocument.UNIQUELY_IDENTIFIES);
            if (null != o && !"".equals(o.toString())) {
                dataDocument.setDataId(o.toString());
            }
            dataDocument.setData(item);
            return dataDocument;
        }).collect(Collectors.toList());
    }

    /**
     * 对象转Doc
     *
     * @param data Map
     * @return List
     */
    public static List<DataDocument> object2DataDocuments(List<Object> data) {
        return data.stream().map(obj -> {
            Map<String, Object> item = BeanMap.create(obj);
            DataDocument dataDocument = new DataDocument();
            Object o = item.get(DataDocument.UNIQUELY_IDENTIFIES);
            if (null != o && !"".equals(o.toString())) {
                dataDocument.setDataId(o.toString());
            }
            dataDocument.setData(item);
            return dataDocument;
        }).collect(Collectors.toList());
    }

    /**
     * 对象转Doc
     *
     * @param article Map
     * @return List
     */
    public static List<Document> map2Documents(List<? extends Map<String, Object>> article) {
        List<Document> documents = new ArrayList<>(article.size());
        for (Map<String, Object> objectMap : article) {
            documents.add(map2Document(objectMap));
        }
        return documents;
    }

    /**
     * 对象转Doc
     *
     * @param article Map
     * @return Document
     */
    public static Document map2Document(Map<String, Object> article) {
        if (!article.containsKey(ID)) {
            throw new NullPointerException("Data needs to contain a unique ID");
        }
        Document document = new Document();

        for (Map.Entry<String, Object> entry : article.entrySet()) {
            TextField textField = new TextField(entry.getKey(), entry.getValue() + "", Store.YES);
            document.add(textField);
        }
        TextField createTimeField = new TextField(CREATE_TIME, System.currentTimeMillis() + "", Store.YES);
        document.add(createTimeField);

        return document;
    }

    /**
     * DataDocumenDataDocument 转 document
     *
     * @param dataDocument 文档
     * @return 文档
     */
    public static Iterable<? extends IndexableField> map2Document(DataDocument dataDocument) {
        if (StringUtils.isNullOrEmpty(dataDocument.getDataId())) {
            throw new NullPointerException("Data needs to contain a unique dataId");
        }
        Document document = new Document();

        for (Map.Entry<String, Object> entry : dataDocument.getData().entrySet()) {
            Object value = entry.getValue();
            Field textField = null;
            if (value instanceof String) {
                textField = new TextField(entry.getKey(), entry.getValue() + "", Store.YES);
            } else if (value instanceof Integer) {
                textField = new LongField(entry.getKey(), (Integer) entry.getValue());
            } else if (value instanceof Long) {
                textField = new LongField(entry.getKey(), (Long) entry.getValue());
            } else if (value instanceof Float) {
                textField = new FloatField(entry.getKey(), (Float) entry.getValue());
            } else if (value instanceof Double) {
                textField = new DoubleField(entry.getKey(), (Double) entry.getValue());
            } else if (value instanceof byte[]) {
                textField = new StoredField(entry.getKey(), (byte[]) entry.getValue());
            } else if (value instanceof IndexableField) {
                textField = (Field) value;
            }
            document.add(textField);
        }
        Field createTimeField = new StringField(CREATE_TIME, System.currentTimeMillis() + "", Store.NO);
        document.add(createTimeField);
        Field idField = new StringField("#" + ID, dataDocument.getDataId(), Store.NO);
        document.add(idField);

        return document;
    }
}