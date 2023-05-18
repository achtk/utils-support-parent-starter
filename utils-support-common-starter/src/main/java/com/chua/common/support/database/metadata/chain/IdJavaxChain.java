package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.context.constant.ContextConstant;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Primary;
import com.chua.common.support.utils.AnnotationUtils;
import com.google.common.base.Strings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * column
 */
@SuppressWarnings("ALL")
public class IdJavaxChain implements ColumnChain{


    @Override
    public void chain(Column column, Field field, AnnotationAttributes javaxColumn) {
        analysisOther(column, javaxColumn, AnnotationUtils.getAnnotationAttributes(field, ContextConstant.GENERATED_VALUE));
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ContextConstant.ID;
    }

    /**
     * 分析主键
     *
     * @param column               字段
     * @param id                   主键
     * @param annotationAttributes
     */
    protected void analysisOther(Column column, AnnotationAttributes id, AnnotationAttributes annotationAttributes) {
        if (null == id) {
            return;
        }

        String strategy = annotationAttributes.getString("generator");
        if(Strings.isNullOrEmpty(strategy)) {
            strategy = annotationAttributes.getEnum("strategy").name();
        }

        Primary primary = new Primary();
        primary.setStrategy(strategy);
        column.setPrimary(primary);
    }
}
