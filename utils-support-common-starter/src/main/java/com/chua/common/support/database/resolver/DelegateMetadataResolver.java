package com.chua.common.support.database.resolver;

import com.chua.common.support.database.dialect.Dialect;
import com.chua.common.support.database.executor.DelegateMetadataExecutor;
import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.common.support.database.expression.DataSourceExpression;
import com.chua.common.support.database.metadata.Metadata;

/**
 * hibernate
 *
 * @author CH
 */
public class DelegateMetadataResolver implements MetadataResolver {
    @Override
    public MetadataExecutor resolve(Metadata<?> metadata, Dialect dialect) {
        return new DelegateMetadataExecutor(new DataSourceExpression(metadata, dialect));
    }
}
