package com.chua.common.support.task.disruptor;

import com.chua.common.support.utils.Preconditions;

import java.util.function.Consumer;

/**
 * 构造器
 *
 * @author CH
 * @since 2022-04-29
 */
public class EventActorBuilder<E> {

    private String name;
    private Consumer<E> consumer;

    /**
     * 名称
     *
     * @param name 名称
     * @return this
     */
    public EventActorBuilder<E> name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 监听
     *
     * @param consumer 监听
     * @return this
     */
    public EventActorBuilder<E> listener(Consumer<E> consumer) {
        this.consumer = consumer;
        return this;
    }


    public EventActor<E> build() {
        Preconditions.checkNotNull(name, "name不能为空");

        return new EventActor<E>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void onEvent(E message, long sequence, boolean endOfBatch) throws Exception {
                if (null == consumer) {
                    return;
                }

                consumer.accept(message);
            }
        };
    }
}
