package com.chua.common.support.task.disruptor;

import com.chua.common.support.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 简单事件
 *
 * @author CH
 * @since 2022-04-29
 */
@Slf4j
public class SimpleSleepEventActor<E> implements EventActor<E> {

    private final String name;
    private final long timeout;

    public SimpleSleepEventActor(String name, long timeout) {
        this.name = name;
        this.timeout = timeout;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onEvent(E message, long sequence, boolean endOfBatch) throws Exception {
        log.info("{}消费了{}", name, sequence);
        ThreadUtils.sleepMillisecondsQuietly(timeout);
    }
}
