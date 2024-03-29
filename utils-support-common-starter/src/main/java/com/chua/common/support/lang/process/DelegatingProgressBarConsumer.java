package com.chua.common.support.lang.process;

import java.util.function.Consumer;

/**
 * Progress bar consumer that delegates the progress bar handling to a custom {@link Consumer}.
 *
 * @author Alex Peelman
 * @since 0.8.0
 */
public class DelegatingProgressBarConsumer implements ProgressBarConsumer {

    private final int maxProgressLength;
    private final Consumer<String> consumer;

    public DelegatingProgressBarConsumer(Consumer<String> consumer) {
        this(consumer, TerminalUtils.getTerminalWidth());
    }

    public DelegatingProgressBarConsumer(Consumer<String> consumer, int maxProgressLength) {
        this.maxProgressLength = maxProgressLength;
        this.consumer = consumer;
    }

    @Override
    public int getMaxRenderedLength() {
        return maxProgressLength;
    }

    @Override
    public void accept(String str) {
        this.consumer.accept(str);
    }

    @Override
    public void close() {
        //NOOP
    }
}
