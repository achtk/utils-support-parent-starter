package com.chua.common.support.database.metadata.chain;

import com.chua.common.support.collection.AnnotationAttributes;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Primary;
import com.chua.common.support.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.chua.common.support.context.constant.ContextConstant.TABLE_ID;

/**
 * mybatis column
 */
@SuppressWarnings("ALL")
public class IdMybatisChain implements ColumnChain{


    @Override
    public void chain(Column column, Field field, AnnotationAttributes tableField) {
        analysisOther(column, AnnotationUtils.getAnnotationAttributes(field, TABLE_ID));
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return TABLE_ID;
    }

    /**
     * 分析主键
     *
     * @param column 字段
     * @param id     主键
     */
    protected void analysisOther(Column column, AnnotationAttributes id) {
        if (null == id || id.isEmpty()) {
            return;
        }

        Primary primary = new Primary();
        Enum<?> type = id.getEnum("type");
        if(null == type || type.ordinal() == 0) {
            primary.setStrategy("increment");
        }
        column.setPrimary(primary);
    }
}
