package com.chua.lucene.support.field;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * double
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/22
 */
public class DoubleField extends Field {
    public static final FieldType TYPE_NOT_STORED = new FieldType();
    public static final FieldType TYPE_STORED;

    public DoubleField(String name, double value, Store stored) {
        super(name, stored == Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
        this.fieldsData = value;
    }
    public DoubleField(String name, double value) {
        super(name, TYPE_STORED);
        this.fieldsData = value;
    }
    public DoubleField(String name, double value, FieldType type) {
        super(name, type);
        this.fieldsData = value;
    }

    static {
        TYPE_NOT_STORED.setTokenized(true);
        TYPE_NOT_STORED.setOmitNorms(true);
        TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS);
        TYPE_NOT_STORED.freeze();
        TYPE_STORED = new FieldType();
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setOmitNorms(true);
        TYPE_STORED.setIndexOptions(IndexOptions.DOCS);
        TYPE_STORED.setStored(true);
        TYPE_STORED.freeze();
    }
}