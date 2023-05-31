package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * column
 *
 * @author Administrator
 * @see com.chua.common.support.database.annotation.Column
 */
public class ColumnSwaggerColumnChain implements ColumnChain{
    @Override
    public void chain(Column column, Field field, AnnotationAttributes annotationAttributes) {
        column.setComment(annotationAttributes.getString("value"));

    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return (Class<? extends Annotation>) ClassUtils.forName("io.swagger.annotations.ApiModelProperty");
    }

}
