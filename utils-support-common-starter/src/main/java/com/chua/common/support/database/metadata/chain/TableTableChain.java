package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicReference;

/**
 * column
 *
 * @author Administrator
 * @see com.chua.common.support.database.annotation.Column
 */
public class TableTableChain implements TableChain {

    private static final String VALUES = "value";

    @Override
    public void chain(AtomicReference<String> reference, Class<?> type, AnnotationAttributes annotationAttributes) {
        if (!annotationAttributes.isEmpty(VALUES)) {
            reference.set(annotationAttributes.getString(VALUES));
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return com.chua.common.support.database.annotation.Table.class;
    }

}
