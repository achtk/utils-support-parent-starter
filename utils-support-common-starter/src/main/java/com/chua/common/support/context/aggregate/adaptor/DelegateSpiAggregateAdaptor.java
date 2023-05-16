package com.chua.common.support.context.aggregate.adaptor;

import com.chua.common.support.context.aggregate.Aggregate;
import com.chua.common.support.context.definition.AggregateSpiDefinition;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.utils.CollectionUtils;

import java.util.Set;

/**
 * 聚合体对象适配器
 *
 * @author CH
 */
public class DelegateSpiAggregateAdaptor implements SpiAggregateAdaptor {

    @Override
    public Set<TypeDefinition<?>> createDefinition(Aggregate aggregate) {
        return CollectionUtils.newHashSet(new AggregateSpiDefinition<>(aggregate));
    }
}
