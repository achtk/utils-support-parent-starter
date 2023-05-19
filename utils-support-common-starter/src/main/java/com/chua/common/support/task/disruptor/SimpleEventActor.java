package com.chua.common.support.task.disruptor;

import lombok.extern.slf4j.Slf4j;

/**
 * 简单事件
 *
 * @author CH
 * @since 2022-04-29
 */
@Slf4j
public class SimpleEventActor<E> implements EventActor<E> {

    private final String name;

    public SimpleEventActor(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onEvent(E message, long sequence, boolean endOfBatch) throws Exception {
        log.info("{}、{} => 处理完成", sequence, name);
    }
}
