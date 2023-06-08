package com.chua.hibernate.support.database.executor;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/**
 * type
 */
public class HibernateEnumType extends AbstractSingleColumnStandardBasicType<Enum>
        implements DiscriminatorType<Enum> {

    private final Class<?> javaType;

    public HibernateEnumType(Class<?> javaType, SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<Enum> javaTypeDescriptor) {
        super(sqlTypeDescriptor, javaTypeDescriptor);
        this.javaType = javaType;
    }

    @Override
    public String getName() {
        return javaType.getTypeName();
    }

    @Override
    public Enum stringToObject(String xml) throws Exception {
        return null;
    }

    @Override
    public String objectToSQLString(Enum value, Dialect dialect) throws Exception {
        return null;
    }
}
