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

import org.apache.calcite.adapter.enumerable.*;
import org.apache.calcite.config.CalciteSystemProperty;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.MethodCallExpression;
import org.apache.calcite.plan.*;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterImpl;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.util.BuiltInMethod;
import org.apache.calcite.util.Pair;
import org.apache.calcite.util.Util;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.AbstractList;
import java.util.List;

/**
 * Relational expression representing a scan of a table in a Mongo data source.
 */
public class TableToEnumerableConverter
        extends ConverterImpl
        implements EnumerableRel {
    protected TableToEnumerableConverter(
            RelOptCluster cluster,
            RelTraitSet traits,
            RelNode input) {
        super(cluster, ConventionTraitDef.INSTANCE, traits, input);
    }

    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        return new TableToEnumerableConverter(
                getCluster(), traitSet, sole(inputs));
    }

    @Override
    public @Nullable RelOptCost computeSelfCost(RelOptPlanner planner,
                                                RelMetadataQuery mq) {
        return super.computeSelfCost(planner, mq).multiplyBy(.1);
    }

    @Override
    public Result implement(EnumerableRelImplementor implementor, Prefer pref) {
        // Generates a call to "find" or "aggregate", depending upon whether
        // an aggregate is present.
        //
        //   ((MongoTable) schema.getTable("zips")).find(
        //     "{state: 'CA'}",
        //     "{city: 1, zipcode: 1}")
        //
        //   ((MongoTable) schema.getTable("zips")).aggregate(
        //     "{$filter: {state: 'CA'}}",
        //     "{$group: {_id: '$city', c: {$sum: 1}, p: {$sum: "$pop"}}")
        final BlockBuilder list = new BlockBuilder();
        final TableRel.Implementor tableImplementor =
                new TableRel.Implementor(getCluster().getRexBuilder());
        tableImplementor.visitChild(0, getInput());
        final RelDataType rowType = getRowType();
        final PhysType physType =
                PhysTypeImpl.of(
                        implementor.getTypeFactory(), rowType,
                        pref.prefer(JavaRowFormat.ARRAY));
        final Expression fields =
                list.append("fields",
                        constantArrayList(
                                Pair.zip(TableRules.fieldNames(rowType),
                                        new AbstractList<Class>() {
                                            @Override
                                            public Class get(int index) {
                                                return physType.fieldClass(index);
                                            }

                                            @Override
                                            public int size() {
                                                return rowType.getFieldCount();
                                            }
                                        }),
                                Pair.class));
        final Expression table =
                list.append("table",
                        tableImplementor.table.getExpression(
                                AbstractRuleTable.RuleQueryable.class));
        List<String> opList = Pair.right(tableImplementor.list);
        final Expression ops =
                list.append("ops",
                        constantArrayList(opList, String.class));
        Expression enumerable =
                list.append("enumerable",
                        Expressions.call(table,
                                RuleMethod.QUERYABLE_AGGREGATE.method, fields, ops));
        if (CalciteSystemProperty.DEBUG.value()) {
            System.out.println("Table: " + opList);
        }
        Hook.QUERY_PLAN.run(opList);
        list.add(
                Expressions.return_(null, enumerable));
        return implementor.result(physType, list.toBlock());
    }

    /**
     * E.g. {@code constantArrayList("x", "y")} returns
     * "Arrays.asList('x', 'y')".
     *
     * @param values List of values
     * @param clazz  Type of values
     * @return expression
     */
    private static <T> MethodCallExpression constantArrayList(List<T> values,
                                                              Class clazz) {
        return Expressions.call(
                BuiltInMethod.ARRAYS_AS_LIST.method,
                Expressions.newArrayInit(clazz, constantList(values)));
    }

    /**
     * E.g. {@code constantList("x", "y")} returns
     * {@code {ConstantExpression("x"), ConstantExpression("y")}}.
     */
    private static <T> List<Expression> constantList(List<T> values) {
        return Util.transform(values, Expressions::constant);
    }
}
