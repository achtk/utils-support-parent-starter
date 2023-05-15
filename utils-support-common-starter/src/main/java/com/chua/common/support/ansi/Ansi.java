package com.chua.common.support.ansi;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Provides a fluent API for generating ANSI escape sequences.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @since 1.0
 */
public class Ansi {

    private static final char FIRST_ESC_CHAR = 27;
    private static final char SECOND_ESC_CHAR = '[';


    public static final String DISABLE = Ansi.class.getName() + ".disable";

    private static Callable<Boolean> detector = new Callable<Boolean>() {
        public Boolean call() throws Exception {
            return !Boolean.getBoolean(DISABLE);
        }
    };

    public static void setDetector(final Callable<Boolean> detector) {
        if (detector == null) {
            throw new IllegalArgumentException();
        }
        Ansi.detector = detector;
    }

    public static boolean isDetected() {
        try {
            return detector.call();
        } catch (Exception e) {
            return true;
        }
    }

    private static final InheritableThreadLocal<Boolean> holder = new InheritableThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return isDetected();
        }
    };

    public static void setEnabled(final boolean flag) {
        holder.set(flag);
    }

    public static boolean isEnabled() {
        return holder.get();
    }

    public static Ansi ansi() {
        if (isEnabled()) {
            return new Ansi();
        } else {
            return new NoAnsi();
        }
    }

    public static Ansi ansi(StringBuilder builder) {
        if (isEnabled()) {
            return new Ansi(builder);
        } else {
            return new NoAnsi(builder);
        }
    }

    public static Ansi ansi(int size) {
        if (isEnabled()) {
            return new Ansi(size);
        } else {
            return new NoAnsi(size);
        }
    }

    private static class NoAnsi
            extends Ansi {
        public NoAnsi() {
            super();
        }

        public NoAnsi(int size) {
            super(size);
        }

        public NoAnsi(StringBuilder builder) {
            super(builder);
        }

        @Override
        public Ansi fg(AnsiElement color) {
            return this;
        }

        @Override
        public Ansi bg(AnsiElement color) {
            return this;
        }

        @Override
        public Ansi fgBright(AnsiElement color) {
            return this;
        }

        @Override
        public Ansi bgBright(AnsiElement color) {
            return this;
        }

        @Override
        public Ansi a(AnsiAttribute attribute) {
            return this;
        }

        @Override
        public Ansi cursor(int x, int y) {
            return this;
        }

        @Override
        public Ansi cursorToColumn(int x) {
            return this;
        }

        @Override
        public Ansi cursorUp(int y) {
            return this;
        }

        @Override
        public Ansi cursorRight(int x) {
            return this;
        }

        @Override
        public Ansi cursorDown(int y) {
            return this;
        }

        @Override
        public Ansi cursorLeft(int x) {
            return this;
        }

        @Override
        public Ansi cursorDownLine() {
            return this;
        }

        @Override
        public Ansi cursorDownLine(final int n) {
            return this;
        }

        @Override
        public Ansi cursorUpLine() {
            return this;
        }

        @Override
        public Ansi cursorUpLine(final int n) {
            return this;
        }

        @Override
        public Ansi eraseScreen() {
            return this;
        }

        @Override
        public Ansi eraseScreen(AnsiErase kind) {
            return this;
        }

        @Override
        public Ansi eraseLine() {
            return this;
        }

        @Override
        public Ansi eraseLine(AnsiErase kind) {
            return this;
        }

        @Override
        public Ansi scrollUp(int rows) {
            return this;
        }

        @Override
        public Ansi scrollDown(int rows) {
            return this;
        }

        @Override
        public Ansi saveCursorPosition() {
            return this;
        }

        @Override
        @Deprecated
        public Ansi restorCursorPosition() {
            return this;
        }

        @Override
        public Ansi restoreCursorPosition() {
            return this;
        }

        @Override
        public Ansi reset() {
            return this;
        }
    }

    private final StringBuilder builder;
    private final ArrayList<Integer> attributeOptions = new ArrayList<>(5);

    public Ansi() {
        this(new StringBuilder());
    }

    public Ansi(Ansi parent) {
        this(new StringBuilder(parent.builder));
        attributeOptions.addAll(parent.attributeOptions);
    }

    public Ansi(int size) {
        this(new StringBuilder(size));
    }

    public Ansi(StringBuilder builder) {
        this.builder = builder;
    }

    public Ansi fg(AnsiElement color) {
        attributeOptions.add(color.fg());
        return this;
    }

    public Ansi fgBlack() {
        return this.fg(AnsiColor.BLACK);
    }

    public Ansi fgBlue() {
        return this.fg(AnsiColor.BLUE);
    }

    public Ansi fgCyan() {
        return this.fg(AnsiColor.CYAN);
    }

    public Ansi fgDefault() {
        return this.fg(AnsiColor.DEFAULT);
    }

    public Ansi fgGreen() {
        return this.fg(AnsiColor.GREEN);
    }

    public Ansi fgMagenta() {
        return this.fg(AnsiColor.MAGENTA);
    }

    public Ansi fgRed() {
        return this.fg(AnsiColor.RED);
    }

    public Ansi fgYellow() {
        return this.fg(AnsiColor.YELLOW);
    }

    public Ansi bg(AnsiElement color) {
        attributeOptions.add(color.bg());
        return this;
    }

    public Ansi bgCyan() {
        return this.bg(AnsiColor.CYAN);
    }

    public Ansi bgDefault() {
        return this.bg(AnsiColor.DEFAULT);
    }

    public Ansi bgGreen() {
        return this.bg(AnsiColor.GREEN);
    }

    public Ansi bgMagenta() {
        return this.bg(AnsiColor.MAGENTA);
    }

    public Ansi bgRed() {
        return this.bg(AnsiColor.RED);
    }

    public Ansi bgYellow() {
        return this.bg(AnsiColor.YELLOW);
    }

    public Ansi fgBright(AnsiElement color) {
        attributeOptions.add(color.fgBright());
        return this;
    }

    public Ansi fgBrightBlack() {
        return this.fgBright(AnsiColor.BLACK);
    }

    public Ansi fgBrightBlue() {
        return this.fgBright(AnsiColor.BLUE);
    }

    public Ansi fgBrightCyan() {
        return this.fgBright(AnsiColor.CYAN);
    }

    public Ansi fgBrightDefault() {
        return this.fgBright(AnsiColor.DEFAULT);
    }

    public Ansi fgBrightGreen() {
        return this.fgBright(AnsiColor.GREEN);
    }

    public Ansi fgBrightMagenta() {
        return this.fgBright(AnsiColor.MAGENTA);
    }

    public Ansi fgBrightRed() {
        return this.fgBright(AnsiColor.RED);
    }

    public Ansi fgBrightYellow() {
        return this.fgBright(AnsiColor.YELLOW);
    }

    public Ansi bgBright(AnsiElement color) {
        attributeOptions.add(color.bgBright());
        return this;
    }

    public Ansi bgBrightCyan() {
        return this.fgBright(AnsiColor.CYAN);
    }

    public Ansi bgBrightDefault() {
        return this.bgBright(AnsiColor.DEFAULT);
    }

    public Ansi bgBrightGreen() {
        return this.bgBright(AnsiColor.GREEN);
    }

    public Ansi bgBrightMagenta() {
        return this.bg(AnsiColor.MAGENTA);
    }

    public Ansi bgBrightRed() {
        return this.bgBright(AnsiColor.RED);
    }

    public Ansi bgBrightYellow() {
        return this.bgBright(AnsiColor.YELLOW);
    }

    public Ansi a(AnsiAttribute attribute) {
        attributeOptions.add(attribute.value());
        return this;
    }

    public Ansi cursor(final int x, final int y) {
        return appendEscapeSequence('H', x, y);
    }

    public Ansi cursorToColumn(final int x) {
        return appendEscapeSequence('G', x);
    }

    public Ansi cursorUp(final int y) {
        return appendEscapeSequence('A', y);
    }

    public Ansi cursorDown(final int y) {
        return appendEscapeSequence('B', y);
    }

    public Ansi cursorRight(final int x) {
        return appendEscapeSequence('C', x);
    }

    public Ansi cursorLeft(final int x) {
        return appendEscapeSequence('D', x);
    }

    public Ansi cursorDownLine() {
        return appendEscapeSequence('E');
    }

    public Ansi cursorDownLine(final int n) {
        return appendEscapeSequence('E', n);
    }

    public Ansi cursorUpLine() {
        return appendEscapeSequence('F');
    }

    public Ansi cursorUpLine(final int n) {
        return appendEscapeSequence('F', n);
    }

    public Ansi eraseScreen() {
        return appendEscapeSequence('J', AnsiErase.ALL.value());
    }

    public Ansi eraseScreen(final AnsiErase kind) {
        return appendEscapeSequence('J', kind.value());
    }

    public Ansi eraseLine() {
        return appendEscapeSequence('K');
    }

    public Ansi eraseLine(final AnsiErase kind) {
        return appendEscapeSequence('K', kind.value());
    }

    public Ansi scrollUp(final int rows) {
        return appendEscapeSequence('S', rows);
    }

    public Ansi scrollDown(final int rows) {
        return appendEscapeSequence('T', rows);
    }

    public Ansi saveCursorPosition() {
        return appendEscapeSequence('s');
    }

    @Deprecated
    public Ansi restorCursorPosition() {
        return appendEscapeSequence('u');
    }

    public Ansi restoreCursorPosition() {
        return appendEscapeSequence('u');
    }

    public Ansi reset() {
        return a(AnsiAttribute.RESET);
    }

    public Ansi bold() {
        return a(AnsiAttribute.INTENSITY_BOLD);
    }

    public Ansi boldOff() {
        return a(AnsiAttribute.INTENSITY_BOLD_OFF);
    }

    public Ansi a(String value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(boolean value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(char value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(char[] value, int offset, int len) {
        flushAttributes();
        builder.append(value, offset, len);
        return this;
    }

    public Ansi a(char[] value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(CharSequence value, int start, int end) {
        flushAttributes();
        builder.append(value, start, end);
        return this;
    }

    public Ansi a(CharSequence value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(double value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(float value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(int value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(long value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(Object value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi a(StringBuffer value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    public Ansi newline() {
        flushAttributes();
        builder.append(System.getProperty("line.separator"));
        return this;
    }

    public Ansi format(String pattern, Object... args) {
        flushAttributes();
        builder.append(String.format(pattern, args));
        return this;
    }


    @Override
    public String toString() {
        flushAttributes();
        return builder.toString();
    }

    ///////////////////////////////////////////////////////////////////
    // Private Helper Methods
    ///////////////////////////////////////////////////////////////////

    private Ansi appendEscapeSequence(char command) {
        flushAttributes();
        builder.append(FIRST_ESC_CHAR);
        builder.append(SECOND_ESC_CHAR);
        builder.append(command);
        return this;
    }

    private Ansi appendEscapeSequence(char command, int option) {
        flushAttributes();
        builder.append(FIRST_ESC_CHAR);
        builder.append(SECOND_ESC_CHAR);
        builder.append(option);
        builder.append(command);
        return this;
    }

    private Ansi appendEscapeSequence(char command, Object... options) {
        flushAttributes();
        return _appendEscapeSequence(command, options);
    }

    private void flushAttributes() {
        if (attributeOptions.isEmpty()) {
            return;
        }
        if (attributeOptions.size() == 1 && attributeOptions.get(0) == 0) {
            builder.append(FIRST_ESC_CHAR);
            builder.append(SECOND_ESC_CHAR);
            builder.append('m');
        } else {
            _appendEscapeSequence('m', attributeOptions.toArray());
        }
        attributeOptions.clear();
    }

    private Ansi _appendEscapeSequence(char command, Object... options) {
        builder.append(FIRST_ESC_CHAR);
        builder.append(SECOND_ESC_CHAR);
        int size = options.length;
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                builder.append(';');
            }
            if (options[i] != null) {
                builder.append(options[i]);
            }
        }
        builder.append(command);
        return this;
    }

}