package com.chua.common.support.task.scheduler.strategy;

import java.util.*;

/**
 * 任务策略
 *
 * @author CH
 */
public final class TimeOrderStrategy<T> implements TimeStrategy<T> {

    private Comparator<T> comparator;

    public TimeOrderStrategy(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public TimeOrderStrategy() {
    }

    @Override
    public List<T> explain(Collection<T> collection) {
        if (null == collection || null == comparator) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(collection);
        list.sort(comparator);
        return list;
    }
}
