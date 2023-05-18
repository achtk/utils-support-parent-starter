package com.chua.hibernate.support.database.executor;

import org.hibernate.boot.spi.AbstractDelegatingMetadata;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * @author Administrator
 */
public class HibernateSimpleMetadata extends AbstractDelegatingMetadata {

    private final DefaultIdentifierGeneratorFactory identifierGeneratorFactory;
    private final TypeConfiguration typeConfiguration;

    public HibernateSimpleMetadata(MetadataImplementor delegate) {
        super(delegate);
        this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
        this.typeConfiguration = new TypeConfiguration();
    }

    @Override
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        return identifierGeneratorFactory;
    }

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return typeConfiguration;
    }
}
