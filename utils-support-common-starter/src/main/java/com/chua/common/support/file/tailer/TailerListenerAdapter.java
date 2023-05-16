package com.chua.common.support.file.tailer;

/**
 * commons-io
 *
 * @author CH
 */
public class TailerListenerAdapter implements TailerListener {

    @Override
    public void init(final Tailer tailer) {
        // noop
    }

    @Override
    public void fileNotFound() {
        // noop
    }

    @Override
    public void handle(String line) {
        // noop
    }

    @Override
    public void handle(Exception ex) {
        // noop
    }
}
