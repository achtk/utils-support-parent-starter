package com.chua.hibernate.support.database.resolver;

import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.common.support.database.expression.DataSourceExpression;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.resolver.MetadataResolver;
import com.chua.common.support.database.sqldialect.Dialect;
import com.chua.hibernate.support.database.executor.HibernateMetadataExecutor;

/**
 * hibernate
 *
 * @author CH
 */
public class HibernateMetadataResolver implements MetadataResolver {
    @Override
    public MetadataExecutor resolve(Metadata<?> metadata, Dialect dialect) {
        return new HibernateMetadataExecutor(new DataSourceExpression(metadata, dialect));
    }
}
