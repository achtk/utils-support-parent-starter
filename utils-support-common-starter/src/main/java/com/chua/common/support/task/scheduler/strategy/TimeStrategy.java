package com.chua.common.support.task.scheduler.strategy;

import java.util.Collection;
import java.util.List;

/**
 * 任务策略
 *
 * @author CH
 */
@FunctionalInterface
public interface TimeStrategy<T> {

    /**
     * 解释策略
     *
     * @param collection 集合
     * @return 策略
     */
    List<T> explain(Collection<T> collection);
}
