package com.chua.datasource.support.table;

import org.apache.calcite.adapter.java.AbstractQueryableTable;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableModify;
import org.apache.calcite.rel.logical.LogicalTableModify;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.ModifiableTable;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TransientTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public abstract class AbstractFactoryTable extends AbstractQueryableTable
        implements TransientTable, ModifiableTable, ScannableTable {

    private final Map<String, SqlTypeName> types;

    public AbstractFactoryTable(Type elementType, Map<String, SqlTypeName> types) {
        super(elementType);
        this.types = types;
    }

    @Override
    public <T> Queryable<T> asQueryable(QueryProvider queryProvider, SchemaPlus schema, String tableName) {
        return null;
    }

    @Override
    public TableModify toModificationRel(RelOptCluster cluster, RelOptTable table, Prepare.CatalogReader catalogReader, RelNode child, TableModify.Operation operation, @Nullable List<String> updateColumnList, @Nullable List<RexNode> sourceExpressionList, boolean flattened) {
        return LogicalTableModify.create(table, catalogReader, child, operation,
                updateColumnList, sourceExpressionList, flattened);
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        List<RelDataType> relDataTypes = new LinkedList<>();
        for (Map.Entry<String, SqlTypeName> entry : types.entrySet()) {
            RelDataType sqlType = typeFactory.createSqlType(entry.getValue());
            sqlType = typeFactory.createTypeWithNullability(sqlType, true);
            relDataTypes.add(sqlType);

        }
        return typeFactory.createStructType(relDataTypes, new LinkedList<>(types.keySet()));
    }


}
