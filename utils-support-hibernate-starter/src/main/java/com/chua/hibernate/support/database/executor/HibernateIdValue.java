package com.chua.hibernate.support.database.executor;

import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.JdbcType;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.ClassUtils;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.hql.internal.antlr.SqlTokenTypes;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.type.AnyType;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.StringNVarcharType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

import java.sql.SQLType;

/**
 * @author Administrator
 */
public class HibernateIdValue extends SimpleValue {
    private Column o;
    private MetadataImplementor metadata;

    @Setter
    @Getter
    private int typeCode;

    public HibernateIdValue(Column o, MetadataImplementor metadata) {
        super(metadata);
        this.o = o;
        this.metadata = metadata;
        if(null != o && null != o.getJavaType()) {
            setTypeName(o.getJavaType().getTypeName());
        }
    }

    @Override
    public Type getType() throws MappingException {
        try {
            SqlTypeDescriptor sqlTypeDescriptor = metadata.getTypeConfiguration().getSqlTypeDescriptorRegistry()
                    .getDescriptor(getTypeCode());
            return super.getType();
        } catch (Exception ignored) {
        }

        JdbcType jdbcType = o.getJdbcType();
        if(null != jdbcType) {
            if(jdbcType == JdbcType.BIGINT || jdbcType == JdbcType.BIG_INTEGER) {
                return BigIntegerType.INSTANCE;
            }
        }

        if(null != o.getJdbcType() && ClassUtils.isPresent("org.hibernate.type." +
                NamingCase.toFirstUpperCase(NamingCase.toCamelCase(o.getJdbcType().name().toLowerCase())) + "Type")) {

        }
        return StringNVarcharType.INSTANCE;
    }

    @Override
    public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
        identifierGeneratorFactory.setDialect(dialect);
        try {
            try {
                return null != identifierGeneratorFactory.getIdentifierGeneratorClass(getIdentifierGeneratorStrategy().replace("ASSIGN_", "").toLowerCase());
            } catch (Exception e) {
                if(getIdentifierGeneratorStrategy().equalsIgnoreCase("AUTO")) {
                    setIdentifierGeneratorStrategy("increment");
                }
                return null != identifierGeneratorFactory.getIdentifierGeneratorClass(getIdentifierGeneratorStrategy());
            }
        } catch (Exception e) {
            return false;
        }
    }
}
