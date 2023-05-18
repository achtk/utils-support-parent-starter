package com.chua.datasource.support.table;

import lombok.AllArgsConstructor;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.sql.type.SqlTypeName;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 内存表
 *
 * @author CH
 */
public class MemTable extends AbstractFactoryTable {
    private final List target;
    private final Map<String, Field> typeAndType;

    public MemTable(Map<String, SqlTypeName> types, List<?> target, Map<String, Field> typeAndType) {
        super(Object[].class, types);
        this.target = target;
        this.typeAndType = typeAndType;
    }

    @Override
    public @Nullable Collection getModifiableCollection() {
        return target;
    }


    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        return new CalciteTableQueryable<>(target);
    }

    /**
     * @param <T>
     */
    @SuppressWarnings("ALL")
    @AllArgsConstructor
    private class CalciteTableQueryable<T> extends AbstractEnumerable<T> {
        private final List<?> target;

        @Override
        public Enumerator<T> enumerator() {
            return (Enumerator<T>) Linq4j.enumerator(target);
        }


    }
}
