package com.chua.hibernate.support.database.executor;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;

/**
 * type
 */
public class HibernateEnumJavaTypeDescriptor implements JavaTypeDescriptor<Enum> {

    @Override
    public Class<Enum> getJavaTypeClass() {
        return null;
    }

    @Override
    public Enum fromString(String string) {
        return null;
    }

    @Override
    public <X> X unwrap(Enum value, Class<X> type, WrapperOptions options) {
        return null;
    }

    @Override
    public <X> Enum wrap(X value, WrapperOptions options) {
        return null;
    }
}