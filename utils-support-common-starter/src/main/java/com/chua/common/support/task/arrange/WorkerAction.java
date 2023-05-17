package com.chua.common.support.task.arrange;

import java.util.Map;

/**
 * 每个最小执行单元需要实现该接口
 *
 * @author wuweifeng wrote on 2019-11-19.
 */
@FunctionalInterface
public interface WorkerAction<T, V> {
    /**
     * 在这里做耗时操作，如rpc请求、IO等
     *
     * @param object      object
     * @param allWrappers 任务包装
     */
    V action(T object, Map<String, Worker> allWrappers);

    /**
     * 超时、异常时，返回的默认值
     *
     * @return 默认值
     */
    default V defaultValue() {
        return null;
    }

}
