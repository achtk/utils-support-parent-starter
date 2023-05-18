package com.chua.common.support.database.resolver;

import com.chua.common.support.database.dialect.Dialect;
import com.chua.common.support.database.executor.DataSourceMetadataExecutor;
import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.common.support.database.expression.DataSourceExpression;
import com.chua.common.support.database.metadata.Metadata;

/**
 * 元数据解析器
 *
 * @author CH
 */
public class DataSourceMetadataResolver implements MetadataResolver {

    @Override
    public MetadataExecutor resolve(Metadata<?> metadata, Dialect dialect) {
        return new DataSourceMetadataExecutor(new DataSourceExpression(metadata, dialect));
    }
}
