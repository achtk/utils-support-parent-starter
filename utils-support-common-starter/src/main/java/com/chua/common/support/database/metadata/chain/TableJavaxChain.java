package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicReference;

/**
 * column
 * @see  com.chua.common.support.database.annotation.Column
 */
@SuppressWarnings("ALL")
public class TableJavaxChain implements TableChain{

    public static final Class<Annotation> TABLE = (Class<Annotation>) ClassUtils.forName("javax.persistence.Table");

    @Override
    public void chain(AtomicReference<String> reference, Class<?> type, AnnotationAttributes annotationAttributes) {
        if(!annotationAttributes.isEmpty("value")) {
            reference.set(annotationAttributes.getString("value"));
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return TABLE;
    }

}
