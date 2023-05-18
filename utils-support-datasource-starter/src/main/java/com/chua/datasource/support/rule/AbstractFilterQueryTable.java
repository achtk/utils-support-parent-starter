package com.chua.datasource.support.rule;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.FilterableTable;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 查询表
 *
 * @author CH
 * @since 2022-03-15
 */
public abstract class AbstractFilterQueryTable extends AbstractQueryTable
        implements FilterableTable {
    protected AbstractFilterQueryTable(Type elementType) {
        super(elementType);
    }

    public AbstractFilterQueryTable() {
        super(Object[].class);
    }

    protected abstract Enumerable<Object[]> getEnumerator(DataContext root, List<RexNode> filters);


    @Override
    public Enumerable<Object[]> scan(DataContext root, List<RexNode> filters) {
        return getEnumerator(root, filters);
    }
}
