package com.chua.hibernate.support.database.executor;

import com.chua.common.support.database.entity.Column;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.SimpleValue;

/**
 * @author Administrator
 */
public class HibernateIdValue extends SimpleValue {
    public HibernateIdValue(Column o, MetadataImplementor metadata) {
        super(metadata);
        setTypeName(o.getJavaType().getTypeName());
    }

    @Override
    public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
        identifierGeneratorFactory.setDialect(dialect);
        return null != identifierGeneratorFactory.getIdentifierGeneratorClass(getIdentifierGeneratorStrategy());
    }
}
