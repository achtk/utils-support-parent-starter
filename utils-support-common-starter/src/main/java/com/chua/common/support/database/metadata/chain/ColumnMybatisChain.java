package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.context.constant.ContextConstant;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.JdbcType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * mybatis column
 */
@SuppressWarnings("ALL")
public class ColumnMybatisChain implements ColumnChain{


    @Override
    public void chain(Column column, Field field, AnnotationAttributes tableField) {
        column.setExist(tableField.getBoolean("exist", true));
        try {
            column.setJdbcType(JdbcType.valueOf(tableField.getEnum("jdbcType").name()));
        } catch (Exception e) {
        }

        if((tableField.isEmpty("value"))) {
            column.setName(tableField.getString("value"));
        }

        column.setFieldName(field.getName());
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ContextConstant.TABLE_FIELD;
    }

}
