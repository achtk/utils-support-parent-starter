package com.chua.common.support.task.scheduler.strategy;

import com.chua.common.support.utils.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 任务策略
 *
 * @author CH
 */
public final class TimeLastStrategy<T> implements TimeStrategy<T> {
    @Override
    public List<T> explain(Collection<T> collection) {
        if (null == collection || collection.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.singletonList(CollectionUtils.findLast(collection));
    }
}
