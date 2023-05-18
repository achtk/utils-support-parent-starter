package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicReference;

/**
 * column
 * @see  com.chua.common.support.database.annotation.Column
 */
@SuppressWarnings("ALL")
public class SchemaJavaxChain implements SchemaChain{

    @Override
    public void chain(AtomicReference<String> reference, Class<?> type, AnnotationAttributes annotationAttributes) {
        if(!annotationAttributes.isEmpty("schema")) {
            reference.set(annotationAttributes.getString("schema"));
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return TableJavaxChain.TABLE;
    }

}
