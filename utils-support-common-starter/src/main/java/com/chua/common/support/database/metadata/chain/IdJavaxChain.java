package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Primary;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.chua.common.support.constant.ContextConstant.GENERATED_VALUE;
import static com.chua.common.support.constant.ContextConstant.ID;

/**
 * column
 */
@SuppressWarnings("ALL")
public class IdJavaxChain implements ColumnChain {


    @Override
    public void chain(Column column, Field field, AnnotationAttributes javaxColumn) {
        analysisOther(column, javaxColumn, AnnotationUtils.getAnnotationAttributes(field, GENERATED_VALUE));
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ID;
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
        if(StringUtils.isNullOrEmpty(strategy)) {
            strategy = annotationAttributes.getEnum("strategy").name();
        }

        Primary primary = new Primary();
        primary.setStrategy(strategy);
        column.setPrimary(primary);
    }
}
