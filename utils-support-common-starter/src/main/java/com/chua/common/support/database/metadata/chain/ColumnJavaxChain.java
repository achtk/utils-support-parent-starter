package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.chua.common.support.context.constant.ContextConstant.COLUMN;
import static com.chua.common.support.context.constant.ContextConstant.COMMENT;

/**
 * column
 */
@SuppressWarnings("ALL")
public class ColumnJavaxChain implements ColumnChain{

    @Override
    public void chain(Column column, Field field, AnnotationAttributes javaxColumn) {
        if(!javaxColumn.isEmpty("name")) {
            column.setName(javaxColumn.getString("name"));
        }

        column.setUpdatable(javaxColumn.getBoolean("updatable"));
        column.setInsertable(javaxColumn.getBoolean("insertable"));
        column.setNullable(javaxColumn.getBoolean("nullable"));

        if(javaxColumn.getIntValue("length", 0) > 0) {
            column.setLength(javaxColumn.getIntValue("length"));
        }

        if(javaxColumn.getIntValue("precision", 0) > 0) {
            column.setPrecision(javaxColumn.getIntValue("precision"));
        }

        if(javaxColumn.getIntValue("scale", 0) > 0) {
            column.setScale(javaxColumn.getIntValue("scale"));
        }

        if(!javaxColumn.isEmpty("columnDefinition")) {
            column.setColumnDefinition(javaxColumn.getString("columnDefinition"));
        }

        AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(field, COMMENT);
        if(null != annotationAttributes) {
            column.setComment(annotationAttributes.getString("value"));
        }

    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return COLUMN;
    }
}
