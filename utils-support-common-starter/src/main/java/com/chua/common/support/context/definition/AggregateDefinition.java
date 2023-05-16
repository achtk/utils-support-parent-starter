package com.chua.common.support.context.definition;

import com.chua.common.support.context.aggregate.Aggregate;

/**
 * 对象
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class AggregateDefinition<T> extends ClassDefinition<T> {

    private final Aggregate aggregate;

    public AggregateDefinition(Aggregate aggregate, Class<T> type, String... name) {
        super(type, name);
        this.aggregate = aggregate;
    }

    /**
     * 原始文件
     * @return 原始文件
     */
    public String getOriginal() {
        return aggregate.getOriginal().toExternalForm();
    }
}
