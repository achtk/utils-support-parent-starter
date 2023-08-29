package com.chua.common.support.lang.formatter;


import com.chua.common.support.utils.StringUtils;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * ddl
 *
 * @author CH
 */
public class DdlFormatter implements Formatter {

    private static final String INITIAL_LINE = System.lineSeparator() + "    ";
    private static final String OTHER_LINES = System.lineSeparator() + "       ";
    /**
     * Singleton access
     */
    public static final DdlFormatter INSTANCE = new DdlFormatter();
    private static final String CREATE_TABLE = "create table";
    private static final String CREATE = "create";
    private static final String ALTER_TABLE = "alter table";
    private static final String COMMENT_ON = "comment on";

    @Override
    public String format(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return sql;
        }
        sql = sql.trim();

        if (sql.toLowerCase(Locale.ROOT).startsWith(CREATE_TABLE)) {
            return formatCreateTable(sql);
        } else if (sql.toLowerCase(Locale.ROOT).startsWith(CREATE)) {
            return sql;
        } else if (sql.toLowerCase(Locale.ROOT).startsWith(ALTER_TABLE)) {
            return formatAlterTable(sql);
        } else if (sql.toLowerCase(Locale.ROOT).startsWith(COMMENT_ON)) {
            return formatCommentOn(sql);
        } else {
            return INITIAL_LINE + sql;
        }
    }

    private String formatCommentOn(String sql) {
        final StringBuilder result = new StringBuilder(60).append(INITIAL_LINE);
        final StringTokenizer tokens = new StringTokenizer(sql, " '[]\"", true);

        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            result.append(token);
            if (isQuote(token)) {
                quoted = !quoted;
            } else if (!quoted) {
                if ("is".equals(token)) {
                    result.append(OTHER_LINES);
                }
            }
        }

        return result.toString();
    }

    private String formatAlterTable(String sql) {
        final StringBuilder result = new StringBuilder(60).append(INITIAL_LINE);
        final StringTokenizer tokens = new StringTokenizer(sql, " (,)'[]\"", true);

        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            if (isQuote(token)) {
                quoted = !quoted;
            } else if (!quoted) {
                if (isBreak(token)) {
                    result.append(OTHER_LINES);
                }
            }
            result.append(token);
        }

        return result.toString();
    }

    private String formatCreateTable(String sql) {
        final StringBuilder result = new StringBuilder(60).append(INITIAL_LINE);
        final StringTokenizer tokens = new StringTokenizer(sql, "(,)'[]\"", true);

        int depth = 0;
        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            if (isQuote(token)) {
                quoted = !quoted;
                result.append(token);
            } else if (quoted) {
                result.append(token);
            } else {
                if (")".equals(token)) {
                    depth--;
                    if (depth == 0) {
                        result.append(INITIAL_LINE);
                    }
                }
                result.append(token);
                if (",".equals(token) && depth == 1) {
                    result.append(OTHER_LINES);
                }
                if ("(".equals(token)) {
                    depth++;
                    if (depth == 1) {
                        result.append(OTHER_LINES);
                    }
                }
            }
        }

        return result.toString();
    }

    private static boolean isBreak(String token) {
        return "drop".equals(token) ||
                "add".equals(token) ||
                "references".equals(token) ||
                "foreign".equals(token) ||
                "on".equals(token);
    }

    private static boolean isQuote(String tok) {
        return "\"".equals(tok) ||
                "`".equals(tok) ||
                "]".equals(tok) ||
                "[".equals(tok) ||
                "'".equals(tok);
    }

}
