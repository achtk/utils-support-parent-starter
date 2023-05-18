package com.chua.common.support.database.metadata;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.context.resolver.NamedResolver;
import com.chua.common.support.context.resolver.factory.SimpleNamedResolver;
import com.chua.common.support.database.annotation.Id;
import com.chua.common.support.database.annotation.Indices;
import com.chua.common.support.database.annotation.Table;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Index;
import com.chua.common.support.database.entity.Primary;
import com.chua.common.support.function.strategy.name.NamedStrategy;
import com.chua.common.support.utils.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 表信息
 *
 * @author CH
 */
public class TypeMetadata<T> extends AbstractMetadata<T> {


    public static <T>TypeMetadata<T> of(Class<T> type) {
        return new TypeMetadata<>(type);
    }

    public TypeMetadata(Class<T> type) {
        super(type);
    }

    public TypeMetadata(Class<T> type, String prefix, String suffix) {
        super(type, prefix, suffix);
    }

    public TypeMetadata(Class<T> type, String suffix) {
        super(type, null, suffix);
    }

    public TypeMetadata(NamedStrategy columnNamedStrategy, NamedStrategy tableNamedStrategy, Class<T> type, boolean isAll) {
        super(columnNamedStrategy, tableNamedStrategy, new SimpleNamedResolver(), type, isAll, null, null);
    }

    public TypeMetadata(NamedStrategy columnNamedStrategy, NamedStrategy tableNamedStrategy, NamedResolver namedResolver, Class<T> type, boolean isAll) {
        super(columnNamedStrategy, tableNamedStrategy, namedResolver, type, isAll, null, null);
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
            Index index1 = createIndices(index);
            if(null == index1) {
                continue;
            }
            rs.add(index1);
        }
        return rs;
    }

    /**
     * 索引
     */
    public static Index createIndices(Indices index) {
        return BeanUtils.copyProperties(index, Index.class);
    }

    @Override
    String analysisDatabase(Class<?> type) {
        Table table1 = type.getDeclaredAnnotation(Table.class);
        return null == table1 ? null : table1.schema();
    }

    @Override
    String analysisTableComment(Class<?> type) {
        Table table1 = type.getDeclaredAnnotation(Table.class);
        return null == table1 ? null : table1.comment();
    }

    @Override
    String analysisTable(Class<?> type, NamedStrategy tableNamedStrategy) {
        Table table1 = type.getDeclaredAnnotation(Table.class);
        return null == table1 ? tableNamedStrategy.named(type.getSimpleName()) : table1.value();
    }

    @Override
    Column analysisColumn(Field field, NamedStrategy columnNamedStrategy) {
        Id id = field.getDeclaredAnnotation(Id.class);
        com.chua.common.support.database.annotation.Column column = field.getDeclaredAnnotation(com.chua.common.support.database.annotation.Column.class);
        if (null == column) {
            if (!isAll) {
                return null;
            }
            Column column1 = new Column();
            column1.setName(columnNamedStrategy.named(field.getName()));
            column1.setJavaType(field.getType());
            column1.setFieldName(field.getName());
            analysisOther(column1, id);
            return column1;
        }


        return analysisColumn(field, id, column);
    }

    protected Column analysisColumn(Field field, Id id, com.chua.common.support.database.annotation.Column column) {
        if(null == column) {
            return new Column();
        }

        Map<String, Object> stringObjectMap = AnnotationUtils.asMap(column);
        Column column1 = BeanUtils.copyProperties(stringObjectMap, Column.class);
        column1.setName(column.value());
        column1.setFieldName(field.getName());
        analysisOther(column1, id);

        return column1;
    }

    /**
     * 分析主键
     *
     * @param column 字段
     * @param id     主键
     */
    protected void analysisOther(Column column, Id id) {
        if (null == id) {
            return;
        }
        Primary primary = new Primary();
        primary.setStrategy(id.strategy());
        column.setPrimary(primary);
    }

    @Override
    public List<Index> getIndex() {
        return Collections.emptyList();
    }
}
