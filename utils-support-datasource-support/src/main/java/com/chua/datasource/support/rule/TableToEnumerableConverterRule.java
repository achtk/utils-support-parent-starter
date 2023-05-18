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

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;

/**
 * Rule to convert a relational expression from
 * {@link RelConvention} to {@link EnumerableConvention}.
 *
 * @author Administrator
 */
public class TableToEnumerableConverterRule extends ConverterRule {
    /**
     * Singleton instance of MongoToEnumerableConverterRule.
     */
    public static ConverterRule INSTANCE1;

    /**
     * Called from the Config.
     */
    protected TableToEnumerableConverterRule(Config config) {
        super(config);
    }

    public static ConverterRule getInstance(String name) {
        if (null == INSTANCE1) {
            INSTANCE1 = Config.INSTANCE
                    .withConversion(RelNode.class, RelConvention.createConvention(name, TableRel.class),
                            EnumerableConvention.INSTANCE, "TableToEnumerableConverterRule")
                    .withRuleFactory(TableToEnumerableConverterRule::new)
                    .toRule(TableToEnumerableConverterRule.class);
        }

        return INSTANCE1;
    }

    @Override
    public RelNode convert(RelNode rel) {
        RelTraitSet newTraitSet = rel.getTraitSet().replace(getOutConvention());
        return new TableToEnumerableConverter(rel.getCluster(), newTraitSet, rel);
    }
}
