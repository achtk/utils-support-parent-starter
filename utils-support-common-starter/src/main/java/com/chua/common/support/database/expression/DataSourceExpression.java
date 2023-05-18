package com.chua.common.support.database.expression;

import com.chua.common.support.database.dialect.Dialect;
import com.chua.common.support.database.metadata.Metadata;

/**
 * 表达式
 * @author CH
 */
public class DataSourceExpression implements Expression{

    private final Metadata<?> metadata;
    private final Dialect dialect;


    public DataSourceExpression(Metadata<?> metadata, Dialect dialect) {
        this.metadata = metadata;
        this.dialect = dialect;
    }


    @Override
    public <T> T getValue(Class<T> type) {
        if(Metadata.class.isAssignableFrom(type)) {
            return (T) metadata;
        }

        if(Dialect.class.isAssignableFrom(type)) {
            return (T) dialect;
        }
        return null;
    }
}
