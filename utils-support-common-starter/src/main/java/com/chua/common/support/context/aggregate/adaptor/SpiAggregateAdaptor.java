package com.chua.common.support.context.aggregate.adaptor;

import com.chua.common.support.context.aggregate.Aggregate;
import com.chua.common.support.context.definition.TypeDefinition;

import java.util.Set;

/**
 * 聚合体对象适配器
 *
 * @author CH
 */
public interface SpiAggregateAdaptor extends AggregateAdaptor{
    /**
     * 定义
     *
     * @param aggregate
     * @return 定义
     */
    Set<TypeDefinition<?>> createDefinition(Aggregate aggregate);
}
