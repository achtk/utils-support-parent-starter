package com.chua.common.support.lang.emoji;

import java.util.regex.Pattern;

/**
 * @author Krishna Chaitanya Thota
 */
public abstract class AbstractEmoji {

    protected static final Pattern SHORT_CODE_PATTERN = Pattern.compile(":(\\w+):");

    protected static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&#\\w+;");

    protected static final Pattern HTML_SURROGATE_ENTITY_PATTERN = Pattern.compile("(?<H>&#\\w+;)(?<L>&#\\w+;)");

    protected static final Pattern HTML_SURROGATE_ENTITY_PATTERN_2 = Pattern.compile("&#\\w+;&#\\w+;&#\\w+;&#\\w+;");

    protected static final Pattern SHORT_CODE_OR_HTML_ENTITY_PATTERN = Pattern.compile(":\\w+:|(?<H1>&#\\w+;)(?<H2>&#\\w+;)(?<L1>&#\\w+;)(?<L2>&#\\w+;)|(?<H>&#\\w+;)(?<L>&#\\w+;)|&#\\w+;");

    /**
     * Helper to convert emoji characters to html entities in a string
     *
     * @param text  String to htmlify
     * @param isHex isHex
     * @return htmlified string
     */
    protected static String htmlHelper(String text, boolean isHex, boolean isSurrogate) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            int ch = text.codePointAt(i);

            boolean b = ch < 159 || ch >= 55296 && ch <= 57343;
            if (ch <= 128) {
                sb.appendCodePoint(ch);
            } else if (b) {
                //
            } else {
                if (isHex) {
                    sb.append("&#x").append(Integer.toHexString(ch)).append(";");
                } else {
                    if (isSurrogate) {
                        double h = Math.floor((ch - 0x10000) / 0x400) + 0xD800;
                        double l = ((ch - 0x10000) % 0x400) + 0xDC00;
                        sb.append("&#").append(String.format("%.0f", h)).append(";&#").append(String.format("%.0f", l)).append(";");
                    } else {
                        sb.append("&#").append(ch).append(";");
                    }
                }
            }

        }

        return sb.toString();
    }

}
