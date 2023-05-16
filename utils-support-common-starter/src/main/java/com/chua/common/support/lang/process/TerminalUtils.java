package com.chua.common.support.lang.process;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * @author Martin Vehovsky
 * @since 0.9.0
 */
public class TerminalUtils {

    static final char CARRIAGE_RETURN = '\r';
    static final char ESCAPE_CHAR = '\u001b';
    static final int DEFAULT_TERMINAL_WIDTH = 120;
    static Queue<ProgressBarConsumer> activeConsumers = new ConcurrentLinkedQueue<>();

    synchronized static int getTerminalWidth() {
        return DEFAULT_TERMINAL_WIDTH;
    }

    static <T extends ProgressBarConsumer> Stream<T> filterActiveConsumers(Class<T> clazz) {
        return activeConsumers.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    static String moveCursorUp(int count) {
        return ESCAPE_CHAR + "[" + count + "A" + CARRIAGE_RETURN;
    }

    static String moveCursorDown(int count) {
        return ESCAPE_CHAR + "[" + count + "B" + CARRIAGE_RETURN;
    }

}
