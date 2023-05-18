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
public class TableMybatisChain implements TableChain{

    public static final Class<Annotation> MYBATIS_TABLE_NAME = (Class<Annotation>) ClassUtils.forName("com.baomidou.mybatisplus.annotation.TableName");

    @Override
    public void chain(AtomicReference<String> reference, Class<?> type, AnnotationAttributes annotationAttributes) {
        if(!annotationAttributes.isEmpty("value")) {
            reference.set(annotationAttributes.getString("value"));
        }
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return MYBATIS_TABLE_NAME;
    }

}
