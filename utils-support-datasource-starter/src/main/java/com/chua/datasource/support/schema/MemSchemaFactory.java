package com.chua.datasource.support.schema;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.context.factory.ApplicationContext;
import com.chua.common.support.table.ConnectorMetadata;
import com.chua.common.support.table.SchemaFactory;
import com.chua.common.support.utils.MapUtils;
import com.chua.datasource.support.SqlNameUtils;
import com.chua.datasource.support.TableUtils;
import com.chua.datasource.support.table.MemTable;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.type.SqlTypeName;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

/**
 * Reflective Schema
 *
 * @author CH
 * @since 2021-11-10
 */
@Spi("mem")
public class MemSchemaFactory extends ReflectiveSchema implements SchemaFactory<Schema> {

    private final Map mapping;
    private Class clazz;
    private List target;
    private String name;
    private SchemaPlus schemaPlus;
    private @MonotonicNonNull Map<String, Table> tableMap;
    private @MonotonicNonNull Multimap<String, Function> functionMap;
    private final Map<String, SqlTypeName> types = new LinkedHashMap<>();
    private final Map<String, Field> typeAndType = new LinkedHashMap<>();

    /**
     * Creates a ReflectiveNewSchema.
     *
     * @param connectorMetadata configureAttributes
     */
    public MemSchemaFactory(ConnectorMetadata connectorMetadata) {
        super(connectorMetadata.get("javaType", Class.class));
        this.schemaPlus = connectorMetadata.get("schemaPlus", SchemaPlus.class);
        this.name = connectorMetadata.get("name", connectorMetadata.get("javaType", Class.class).getSimpleName(), String.class);
        this.clazz = connectorMetadata.get("javaType", Class.class);
        this.target = connectorMetadata.get("data", List.class);
        this.mapping = connectorMetadata.get("mapping", Collections.emptyMap(), Map.class);
        this.doAnalysisMetaData();
        this.target = this.analysisItems(target);
    }

    /**
     * 解析数据
     */
    private void doAnalysisMetaData() {
        for (Object o : target) {
            if (o instanceof Map) {
                ((Map<?, ?>) o).forEach((k, v) -> {
                    String s = LOWER_CAMEL.converterTo(LOWER_UNDERSCORE).convert(k.toString());
                    types.put(s, null == v ? SqlTypeName.ANY : SqlNameUtils.get(v.getClass()));
                    typeAndType.put(MapUtils.getString(mapping, k.toString(), k.toString()), null);
                });
            } else {
                Class<?> aClass = o.getClass();
                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (Modifier.isStatic(declaredField.getModifiers())) {
                        continue;
                    }
                    String s = LOWER_CAMEL.converterTo(LOWER_UNDERSCORE).convert(declaredField.getName());
                    s = MapUtils.getString(mapping, s, s);
                    types.put(s, SqlNameUtils.get(declaredField.getType()));

                    declaredField.setAccessible(true);
                    typeAndType.put(s, declaredField);
                }
            }
            break;
        }

    }


    private List<Object[]> analysisItems(List<?> target) {
        List<Object[]> items = new LinkedList<>();
        for (Object o : target) {
            List<Object> item = new LinkedList<>();
            for (Map.Entry<String, Field> entry : typeAndType.entrySet()) {
                Field value = entry.getValue();
                String key = entry.getKey();
                try {
                    if (o instanceof Map) {
                        item.add((converter(((Map<?, ?>) o).get(key))));
                    } else {
                        item.add(converter(value.get(o)));
                    }
                } catch (Exception e) {
                    item.add(null);
                }
            }

            items.add(item.toArray());
        }
        return items;
    }

    @Override
    protected Map<String, Table> getTableMap() {
        if (tableMap == null) {
            tableMap = createTableMap();
        }
        return tableMap;
    }

    private Map<String, Table> createTableMap() {
        final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
        builder.put(name, new MemTable(types, target, typeAndType));
        return builder.build();
    }


    @Override
    public Schema getSchema() {
        return this;
    }

    @Override
    public String getSchemaName() {
        return name;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return TableUtils.getContext();
    }

    @Override
    public Properties comments() {
        return null;
    }

    @Override
    public Map getTables() {
        return getTableMap();
    }


    /**
     * 转化
     *
     * @param o   值
     * @param <E> 类型
     * @return 结果
     */
    private <E> Object converter(Object o) {
        if (o instanceof Date) {
            return new java.sql.Date(((Date) o).getTime());
        }

        if (o instanceof Iterable) {
            return Joiner.on(",").join((Iterable<?>) o);
        }
        return o;
    }


}
