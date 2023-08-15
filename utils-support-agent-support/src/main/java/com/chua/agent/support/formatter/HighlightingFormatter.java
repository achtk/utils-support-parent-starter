package com.chua.agent.support.formatter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * 高亮
 *
 * @author CH
 */
public class HighlightingFormatter implements Formatter {
    public static final String WHITESPACE = " \n\r\f\t";
    public static final Formatter INSTANCE =
            // blue
            // cyan
            new HighlightingFormatter(
                    "34",
                    "36",
                    "32"
            );
    private static final Set<String> KEYWORDS = new HashSet<>(AnsiSqlKeywords.INSTANCE.sql2003());
    private static final String SYMBOLS_AND_WS = "=><!+-*/()',.|&`\"?" + WHITESPACE;

    static {
        // additional keywords not reserved by ANSI SQL 2003
        KEYWORDS.addAll(Arrays.asList("KEY", "SEQUENCE", "CASCADE", "INCREMENT"));
    }

    private final String keywordEscape;
    private final String stringEscape;
    private final String quotedEscape;
    private final String normalEscape;

    /**
     * @param keywordCode the ANSI escape code to use for highlighting SQL keywords
     * @param stringCode  the ANSI escape code to use for highlighting SQL strings
     */
    public HighlightingFormatter(String keywordCode, String stringCode, String quotedCode) {
        keywordEscape = escape(keywordCode);
        stringEscape = escape(stringCode);
        quotedEscape = escape(quotedCode);
        normalEscape = escape("0");
    }

    private static String escape(String code) {
        return "\u001b[" + code + "m";
    }

    @Override
    public String format(String sql) {
        sql = sql.trim();
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean inQuoted = false;
        for (StringTokenizer tokenizer = new StringTokenizer(sql, SYMBOLS_AND_WS, true);
             tokenizer.hasMoreTokens(); ) {
            String token = tokenizer.nextToken();
            // for MySQL
            switch (token) {
                case "\"":
                case "`":
                    if (inString) {
                        result.append(token);
                    } else if (inQuoted) {
                        inQuoted = false;
                        result.append(token).append(normalEscape);
                    } else {
                        inQuoted = true;
                        result.append(quotedEscape).append(token);
                    }
                    break;
                case "'":
                    if (inQuoted) {
                        result.append('\'');
                    } else if (inString) {
                        inString = false;
                        result.append('\'').append(normalEscape);
                    } else {
                        inString = true;
                        result.append(stringEscape).append('\'');
                    }
                    break;
                default:
                    if (KEYWORDS.contains(token.toUpperCase())) {
                        result.append(keywordEscape).append(token).append(normalEscape);
                    } else {
                        result.append(token);
                    }
            }
        }
        return result.toString();
    }

}
