package com.chua.common.support.database.metadata;

import com.chua.common.support.database.annotation.Indices;
import com.chua.common.support.database.annotation.Table;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Index;
import com.chua.common.support.database.metadata.chain.ColumnChain;
import com.chua.common.support.database.metadata.chain.SchemaChain;
import com.chua.common.support.database.metadata.chain.TableChain;
import com.chua.common.support.function.strategy.name.NamedStrategy;
import com.chua.common.support.function.strategy.resolver.NamedResolver;
import com.chua.common.support.function.strategy.resolver.SimpleNamedResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 表信息
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class DelegateMetadata<T> extends TypeMetadata<T> {



    public DelegateMetadata(Class<T> type) {
        super(type);
    }

    public DelegateMetadata(Class<T> type, String prefix, String suffix) {
        super(type, prefix, suffix);
    }

    public DelegateMetadata(Class<T> type, String suffix) {
        super(type, null, suffix);
    }


    public DelegateMetadata(NamedStrategy columnNamedStrategy, NamedStrategy tableNamedStrategy, Class<T> type, boolean isAll) {
        super(columnNamedStrategy, tableNamedStrategy, new SimpleNamedResolver(), type, isAll);
    }

    public DelegateMetadata(NamedStrategy columnNamedStrategy, NamedStrategy tableNamedStrategy, NamedResolver namedResolver, Class<T> type, boolean isAll) {
        super(columnNamedStrategy, tableNamedStrategy, namedResolver, type, isAll);
    }

    public static <T> TypeMetadata<T> of(Class<T> type) {
        return new TypeMetadata<>(type);
    }

    @Override
    String analysisDefinition(Class<T> type) {
        Table table1 = type.getDeclaredAnnotation(Table.class);
        return null == table1 ? null : table1.definition();
    }

    @Override
    List<Index> analysisIndex(Class<T> type) {
        List<Index> rs = new LinkedList<>();
        Indices[] indices = type.getDeclaredAnnotationsByType(Indices.class);
        for (Indices index : indices) {
            Index index1 = TypeMetadata.createIndices(index);
            if(null == index1) {
                continue;
            }
            rs.add(index1);
        }
        return rs;
    }

    String analysisDatabase(Class<?> type) {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        List<SchemaChain> collect = ServiceProvider.of(SchemaChain.class).collect();
        for (SchemaChain chain : collect) {
            Class aClass = chain.annotationType();
            if (null == aClass) {
                continue;
            }

            Annotation declaredAnnotation = type.getDeclaredAnnotation(aClass);
            if(null == declaredAnnotation) {
                continue;
            }

            chain.chain(atomicReference, type, AnnotationUtils.getAnnotationAttributes(type, aClass));
        }

        return atomicReference.get();
    }

    @Override
    String analysisTable(Class<?> type, NamedStrategy tableNamedStrategy) {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        List<TableChain> collect = ServiceProvider.of(TableChain.class).collect();
        for (TableChain chain : collect) {
            Class aClass = chain.annotationType();
            if (null == aClass) {
                continue;
            }

            Annotation declaredAnnotation = type.getDeclaredAnnotation(aClass);
            if(null == declaredAnnotation) {
                continue;
            }

            chain.chain(atomicReference, type, AnnotationUtils.getAnnotationAttributes(type, aClass));
        }

        return tableNamedStrategy.named(StringUtils.defaultString(atomicReference.get(), type.getSimpleName()));
    }

    @Override
    Column analysisColumn(Field field, NamedStrategy columnNamedStrategy) {
        Column rs = new Column(field);
        rs.setName(columnNamedStrategy.named(field.getName()));
        if(field.getType() == String.class) {
            rs.setLength(255);
        }

        List<ColumnChain> columnChains = ServiceProvider.of(ColumnChain.class).collect();
        for (ColumnChain columnChain : columnChains) {
            Class aClass = columnChain.annotationType();
            if (null == aClass) {
                continue;
            }

            Annotation declaredAnnotation = field.getDeclaredAnnotation(aClass);
            if(null == declaredAnnotation) {
                continue;
            }
            columnChain.chain(rs, field, AnnotationUtils.getAnnotationAttributes(field, aClass));
        }


        return rs;


    }


}
