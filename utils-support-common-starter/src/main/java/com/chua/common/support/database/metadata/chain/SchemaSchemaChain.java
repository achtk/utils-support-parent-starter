package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 表
 *
 * @author CH
 */
public class SchemaSchemaChain implements SchemaChain {

    private static final String SCHEMA = "schema";

    @Override
    public void chain(AtomicReference<String> reference, Class<?> type, AnnotationAttributes annotationAttributes) {
        if (!annotationAttributes.isEmpty(SCHEMA)) {
            reference.set(annotationAttributes.getString(SCHEMA));
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return com.chua.common.support.database.annotation.Table.class;
    }

}
