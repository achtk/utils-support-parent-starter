package com.chua.common.support.ansi;


import java.util.Locale;

import static com.chua.common.support.constant.NumberConstant.ONE_HUNDRED;
import static com.chua.common.support.constant.NumberConstant.TEN;

/**
 * Generates ANSI encoded output, automatically attempting to detect if the terminal
 * supports ANSI.
 *
 * @author Phillip Webb
 * @since 1.0.0
 */
public class AnsiOutput {

    private static final String ENCODE_JOIN = ";";

    private static Enabled enabled = Enabled.DETECT;

    private static Boolean consoleAvailable;

    private static Boolean ansiCapable;

    private static final String OPERATING_SYSTEM_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

    private static final String ENCODE_START = "\033[";

    private static final String ENCODE_END = "m";

    private static final String RESET = "0;" + AnsiColor.DEFAULT;

    /**
     * Sets if ANSI output is enabled.
     *
     * @param enabled if ANSI is enabled, disabled or detected
     */
    public static void setEnabled(Enabled enabled) {
        AnsiOutput.enabled = enabled;
    }


    public static void help() {
        //基本色
        int v15 = 15;
        for (int i = 0; i <= v15; i++) {
            if (i % 8 == 0) {
                System.out.println();
            }
            if (i == 8) {
                System.out.print(ENCODE_START + "38;5;0" + ENCODE_END + fix(0, "38;5;"));
            }
            System.out.print(ENCODE_START + "48;5;" + i + ENCODE_END + fix(i, "48;5;"));
        }
        System.out.print(ENCODE_START + "38;5;15" + ENCODE_END + fix(15, "38;5;"));
        //色谱
        int v16 = 16, v231 = 231, v232 = 232, v255 = 255;
        for (int i = v16; i <= v231; i++) {
            if (i % 8 == 0) {
                System.out.println();
            }
            System.out.print(ENCODE_START + "48;5;" + i + ENCODE_END + fix(i, "48;5;"));
            if ((i - 15) % 36 == 0) {
                System.out.print(ENCODE_START + "38;5;15" + ENCODE_END + fix(15, "38;5;"));
            }
            if ((i - 15) % 36 == 18) {
                System.out.print(ENCODE_START + "38;5;0" + ENCODE_END + fix(0, "38;5;"));
            }
        }
        //灰度
        for (int i = v232; i <= v255; i++) {
            if (i % 8 == 0) {
                System.out.println();
            }
            if (i == 244) {
                System.out.print(ENCODE_START + "38;5;0" + ENCODE_END + fix(0, "38;5;"));
            }
            System.out.print(ENCODE_START + "48;5;" + i + ENCODE_END + fix(i, "48;5;"));
        }
    }

    static String fix(int i, String s) {
        if (i < TEN) {
            return "  " + i + "(" + s + ")  ";
        }
        if (i < ONE_HUNDRED) {
            return "  " + i + "(" + s + ") ";
        }
        return " " + i + "(" + s + ") ";
    }

    /**
     * Returns if ANSI output is enabled
     *
     * @return if ANSI enabled, disabled or detected
     */
    public static Enabled getEnabled() {
        return AnsiOutput.enabled;
    }

    /**
     * Sets if the System.console() is known to be available.
     *
     * @param consoleAvailable if the console is known to be available or {@code null} to
     *                         use standard detection logic.
     */
    public static void setConsoleAvailable(Boolean consoleAvailable) {
        AnsiOutput.consoleAvailable = consoleAvailable;
    }

    /**
     * Encode a single {@link AnsiElement} if output is enabled.
     *
     * @param element the element to encode
     * @return the encoded element or an empty string
     */
    public static String encode(AnsiElement element) {
        if (isEnabled()) {
            return ENCODE_START + element + ENCODE_END;
        }
        return "";
    }

    /**
     * Create a new ANSI string from the specified elements. Any {@link AnsiElement}s will
     * be encoded as required.
     *
     * @param elements the elements to encode
     * @return a string of the encoded elements
     */
    public static String toString(Object... elements) {
        StringBuilder sb = new StringBuilder();
        if (isEnabled()) {
            buildEnabled(sb, elements);
        } else {
            buildDisabled(sb, elements);
        }
        return sb.toString();
    }

    private static void buildEnabled(StringBuilder sb, Object[] elements) {
        boolean writingAnsi = false;
        boolean containsEncoding = false;
        for (Object element : elements) {
            if (element instanceof AnsiElement) {
                containsEncoding = true;
                if (!writingAnsi) {
                    sb.append(ENCODE_START);
                    writingAnsi = true;
                } else {
                    sb.append(ENCODE_JOIN);
                }
            } else {
                if (writingAnsi) {
                    sb.append(ENCODE_END);
                    writingAnsi = false;
                }
            }
            sb.append(element);
        }
        if (containsEncoding) {
            sb.append(writingAnsi ? ENCODE_JOIN : ENCODE_START);
            sb.append(RESET);
            sb.append(ENCODE_END);
        }
    }

    private static void buildDisabled(StringBuilder sb, Object[] elements) {
        for (Object element : elements) {
            if (!(element instanceof AnsiElement) && element != null) {
                sb.append(element);
            }
        }
    }

    private static boolean isEnabled() {
        if (enabled == Enabled.DETECT) {
            if (ansiCapable == null) {
                ansiCapable = detectIfAnsiCapable();
            }
            return ansiCapable;
        }
        return enabled == Enabled.ALWAYS;
    }

    private static boolean detectIfAnsiCapable() {
        try {
            if (Boolean.FALSE.equals(consoleAvailable)) {
                return false;
            }
            if ((consoleAvailable == null) && (System.console() == null)) {
                return false;
            }
            return !(OPERATING_SYSTEM_NAME.contains("win"));
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Possible values to pass to {@link AnsiOutput#setEnabled}. Determines when to output
     * ANSI escape sequences for coloring application output.
     */
    public enum Enabled {

        /**
         * Try to detect whether ANSI coloring capabilities are available. The default
         * value for {@link AnsiOutput}.
         */
        DETECT,

        /**
         * Enable ANSI-colored output.
         */
        ALWAYS,

        /**
         * Disable ANSI-colored output.
         */
        NEVER

    }

}
