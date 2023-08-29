package com.chua.common.support.lang.process;

/**
 * Contains methods to compute the display lengths of characters and strings on a terminal.
 *
 * @author Tongfei Chen
 * @since 0.9.1
 */
class StringDisplayUtils {

    private static char M_SYMBOL = 'm';

    /**
     * Returns the display width of a Unicode character on terminal.
     */
    static int getCharDisplayLength(char c) {
        return 1;
    }

    /**
     * Returns the display width of a Unicode string on terminal.
     */
    static int getStringDisplayLength(String s) {
        int displayWidth = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\033') {
                while (i < s.length() && s.charAt(i) != M_SYMBOL) {
                    i++;
                }
            } else {
                displayWidth += getCharDisplayLength(s.charAt(i));
            }
        }
        return displayWidth;
    }

    static String trimDisplayLength(String s, int maxDisplayLength) {
        if (maxDisplayLength <= 0) {
            return "";
        }

        int totalLength = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\033') {
                while (i < s.length() && s.charAt(i) != M_SYMBOL) {
                    i++;
                }
                i++;  // skip the 'm' character
            }
            totalLength += getCharDisplayLength(s.charAt(i));
            if (totalLength > maxDisplayLength) {
                return s.substring(0, i);
            }
        }
        return s;
    }

}
