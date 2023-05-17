package com.chua.common.support.task.scheduler.strategy;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 任务策略
 *
 * @author CH
 */
public final class TimeBroadcastStrategy<T> implements TimeStrategy<T> {
    @Override
    public List<T> explain(Collection<T> collection) {
        if (null == collection) {
            return Collections.emptyList();
        }

        return new LinkedList<>(collection);
    }
}
