package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Primary;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * column
 *
 * @author Administrator
 * @see com.chua.common.support.database.annotation.Column
 */
public class IdColumnChain implements ColumnChain{
    @Override
    public void chain(Column column, Field field, AnnotationAttributes annotationAttributes) {
        analysisOther(column, annotationAttributes);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return com.chua.common.support.database.annotation.Id.class;
    }

    /**
     * 分析主键
     *
     * @param column 字段
     * @param id     主键
     */
    protected void analysisOther(Column column, AnnotationAttributes id) {
        if (null == id) {
            return;
        }
        Primary primary = new Primary();
        primary.setStrategy(id.getString("strategy"));
        column.setPrimary(primary);
    }
}
