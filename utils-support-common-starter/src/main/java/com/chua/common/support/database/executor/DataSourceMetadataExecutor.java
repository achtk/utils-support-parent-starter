package com.chua.common.support.database.executor;

import com.chua.common.support.constant.Action;
import com.chua.common.support.database.action.DataSourceActionExecutor;
import com.chua.common.support.database.expression.Expression;
import com.chua.common.support.lang.exception.NotSupportedException;

import javax.sql.DataSource;

/**
 * 元数据执行器
 *
 * @author CH
 */
public class DataSourceMetadataExecutor implements MetadataExecutor {


    private final Expression expression;

    public DataSourceMetadataExecutor(Expression expression) {
        this.expression = expression;
    }

    /**
     * 执行器
     *
     * @param dataSource 数据源
     * @param action     动作
     */
    private void execute(DataSource dataSource, Action action) {
        try {
            DataSourceActionExecutor dataSourceActionExecutor = DataSourceActionExecutor.valueOf(action.name());
            dataSourceActionExecutor.doExecute(new DataSourceActionExecutor.DataSourceActionMetadata(dataSource, expression));
        } catch (IllegalArgumentException e) {
            throw new NotSupportedException("不支持 "+ action);
        }
    }

    @Override
    public void execute(Object datasource, Action action) {
        if(null == action) {
            throw new NullPointerException();
        }

        if (null == datasource || datasource instanceof DataSource) {
            execute((DataSource) datasource, action);
            return;
        }
        throw new NotSupportedException("不支持 datasource"+ datasource);

    }
}
