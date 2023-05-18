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
public class TableTableChain implements TableChain{

    @Override
    public void chain(AtomicReference<String> reference, Class<?> type, AnnotationAttributes annotationAttributes) {
        if(!annotationAttributes.isEmpty("value")) {
            reference.set(annotationAttributes.getString("value"));
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return com.chua.common.support.database.annotation.Table.class;
    }

}
