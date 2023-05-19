package com.chua.common.support.task.disruptor;

/**
 * 事件处理器
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/30
 */
public interface EventActor<E> {
    /**
     * 初始化
     *
     * @param <E> e
     * @return this
     */
    static <E> EventActorBuilder<E> newBuilder() {
        return new EventActorBuilder<E>();
    }

    /**
     * 名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 事件
     *
     * @param message    消息
     * @param sequence   序号
     * @param endOfBatch 批次结束
     * @throws Exception Exception
     */
    void onEvent(E message, long sequence, boolean endOfBatch) throws Exception;
}
