/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chua.datasource.support.rule;

import com.chua.common.support.lang.profile.Profile;
import org.apache.calcite.adapter.java.AbstractQueryableTable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.AbstractTableQueryable;
import org.apache.calcite.sql.type.SqlTypeName;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Table based on a MongoDB collection.
 *
 * @author Administrator
 */
public abstract class AbstractRuleTable extends AbstractQueryableTable
        implements TranslatableTable {
    protected final Profile profile;
    private final String tableName;
    private final Map<String, SqlTypeName> types;
    private final Convention convention;

    /**
     * Creates a RuleTable.
     *
     * @param types               字段与类型
     * @param profile 配置
     */
    public AbstractRuleTable(Profile profile, Map<String, SqlTypeName> types) {
        super(Object[].class);
        this.types = types;
        this.profile = profile;
        this.tableName = profile.getString("name");
        this.convention = RelConvention.createConvention(tableName, TableRel.class);
    }

    @Override
    public String toString() {
        return "TableTable {" + tableName + "}";
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

    @Override
    public <T> Queryable<T> asQueryable(QueryProvider queryProvider,
                                        SchemaPlus schema, String tableName) {
        return new RuleQueryable<>(queryProvider, schema, this, tableName);
    }

    @Override
    public RelNode toRel(
            RelOptTable.ToRelContext context,
            RelOptTable relOptTable) {
        final RelOptCluster cluster = context.getCluster();
        return new TableTableScan(cluster, cluster.traitSetOf(convention),
                relOptTable, this, null);
    }

    /**
     * Executes a "find" operation on the underlying collection.
     *
     * <p>For example,
     * <code>zipsTable.find("{state: 'OR'}", "{city: 1, zipcode: 1}")</code></p>
     *
     * @param profile  Table connection
     * @param filterJson  Filter JSON string, or null
     * @param projectJson Project JSON string, or null
     * @param fields      List of fields to project; or null to return map
     * @return Enumerator of results
     */
    protected abstract Enumerable<Object> find(Profile profile, String filterJson, String projectJson, List<Map.Entry<String, Class>> fields);

    /**
     * Executes an "aggregate" operation on the underlying collection.
     *
     * <p>For example:
     * <code>zipsTable.aggregate(
     * "{$filter: {state: 'OR'}",
     * "{$group: {_id: '$city', c: {$sum: 1}, p: {$sum: '$pop'}}}")
     * </code></p>
     *
     * @param profile Table connection
     * @param fields              List of fields to project; or null to return map
     * @param operations          One or more JSON strings
     * @return Enumerator of results
     */
    protected abstract Enumerable<Object> aggregate(final Profile profile,
                                                    final List<Map.Entry<String, Class>> fields,
                                                    final List<String> operations);

    /**
     * Implementation of {@link Queryable} based on
     * a {@link AbstractRuleTable}.
     *
     * @param <T> element type
     */
    public class RuleQueryable<T> extends AbstractTableQueryable<T> {
        public RuleQueryable(QueryProvider queryProvider, SchemaPlus schema,
                             AbstractRuleTable table, String tableName) {
            super(queryProvider, schema, table, tableName);
        }

        @Override
        public Enumerator<T> enumerator() {
            final Enumerable<T> enumerable =
                    (Enumerable<T>) getTable().find(getConfigureAttributes(), null, null, null);
            return enumerable.enumerator();
        }

        private Profile getConfigureAttributes() {
            return profile;
        }

        private AbstractRuleTable getTable() {
            return (AbstractRuleTable) table;
        }

        /**
         * Called via code-generation.
         *
         * @see RuleMethod#QUERYABLE_AGGREGATE
         */
        @SuppressWarnings("UnusedDeclaration")
        public Enumerable aggregate(List<Map.Entry<String, Class>> fields,
                                    List<String> operations) {
            return getTable().aggregate(getConfigureAttributes(), fields, operations);
        }

        /**
         * Called via code-generation.
         *
         * @param filterJson  Filter document
         * @param projectJson Projection document
         * @param fields      List of expected fields (and their types)
         * @return result of mongo query
         * @see RuleMethod#QUERYABLE_FIND
         */
        @SuppressWarnings("UnusedDeclaration")
        public Enumerable<Object> find(String filterJson,
                                       String projectJson, List<Map.Entry<String, Class>> fields) {
            return getTable().find(getConfigureAttributes(), filterJson, projectJson, fields);
        }
    }
}
