package com.chua.common.support.task.arrange;

/**
 * 每个最小执行单元需要实现该接口
 *
 * @author wuweifeng wrote on 2019-11-19.
 */
public interface WorkerBuilder<T, V> {
    /**
     * 构建工作者
     *
     * @param <T> input
     * @param <V> out
     * @return worker builder
     */
    static <T, V> Worker.Builder<T, V> newBuilder() {
        return new Worker.Builder<>();
    }
}
