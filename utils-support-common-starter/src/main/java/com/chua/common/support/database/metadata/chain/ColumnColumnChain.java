package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.database.entity.Column;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * column
 *
 * @author Administrator
 * @see com.chua.common.support.database.annotation.Column
 */
public class ColumnColumnChain implements ColumnChain{
    @Override
    public void chain(Column column, Field field, AnnotationAttributes annotationAttributes) {
        column.setJdbcType(annotationAttributes.getEnum("jdbcType"));
        column.setLength(annotationAttributes.getIntValue("length"));
        column.setName(annotationAttributes.getString("value"));
        column.setComment(annotationAttributes.getString("comment"));
        column.setDefaultValue(annotationAttributes.getString("defaultValue"));

    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return com.chua.common.support.database.annotation.Column.class;
    }

}
