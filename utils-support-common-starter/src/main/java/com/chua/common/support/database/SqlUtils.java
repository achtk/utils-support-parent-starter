package com.chua.common.support.database;

import com.chua.common.support.database.orm.enums.SqlLike;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CharConstant.COLON;
import static com.chua.common.support.constant.CommonConstant.*;

/**
 * SqlUtils工具类
 * !!! 本工具不适用于本框架外的类使用 !!!
 *
 * @author Caratacus
 * @since 2016-11-13
 */
@SuppressWarnings("serial")
public class SqlUtils {

    private static final Pattern PATTERN = Pattern.compile("\\{@((\\w+?)|(\\w+?:\\w+?)|(\\w+?:\\w+?:\\w+?))}");

    /**
     * 用%连接like
     *
     * @param str 原字符串
     * @return like 的值
     */
    public static String concatLike(Object str, SqlLike type) {
        switch (type) {
            case LEFT:
                return SYMBOL_PERCENT + str;
            case RIGHT:
                return str + SYMBOL_PERCENT;
            default:
                return SYMBOL_PERCENT + str + SYMBOL_PERCENT;
        }
    }

    public static List<String> findPlaceholder(String sql) {
        Matcher matcher = PATTERN.matcher(sql);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    public static String replaceSqlPlaceholder(String sql, List<String> placeHolder, String escapeSymbol) {
        for (String s : placeHolder) {
            String s1 = s.substring(2, s.length() - 1);
            int i1 = s1.indexOf(COLON);
            String tableName;
            String alisa = null;
            String asAlisa = null;
            if (i1 < 0) {
                tableName = s1;
            } else {
                tableName = s1.substring(0, i1);
                s1 = s1.substring(i1 + 1);
                i1 = s1.indexOf(COLON);
                if (i1 < 0) {
                    alisa = s1;
                } else {
                    alisa = s1.substring(0, i1);
                    asAlisa = s1.substring(i1 + 1);
                }
            }
            sql = sql.replace(s, getSelectBody(tableName, alisa, asAlisa, escapeSymbol));
        }
        return sql;
    }

    public static String getSelectBody(String tableName, String alisa, String asAlisa, String escapeSymbol) {
        return "";
    }

    public static String getNewSelectBody(String selectBody, String alisa, String asAlisa, String escapeSymbol) {
        String[] split = selectBody.split(SYMBOL_COMMA);
        StringBuilder sb = new StringBuilder();
        boolean asA = asAlisa != null;
        for (String body : split) {
            final String sa = alisa.concat(SYMBOL_DOT);
            if (asA) {
                int as = body.indexOf(SYMBOL_AS);
                if (as < 0) {
                    sb.append(sa).append(body).append(SYMBOL_AS).append(escapeColumn(asAlisa.concat(SYMBOL_DOT).concat(body), escapeSymbol));
                } else {
                    String column = body.substring(0, as);
                    String property = body.substring(as + 4);
                    property = getTargetColumn(property);
                    sb.append(sa).append(column).append(SYMBOL_AS).append(escapeColumn(asAlisa.concat(SYMBOL_DOT).concat(property), escapeSymbol));
                }
            } else {
                sb.append(sa).append(body);
            }
            sb.append(SYMBOL_COMMA);
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private static String escapeColumn(String column, String escapeSymbol) {
        return escapeSymbol.concat(column).concat(escapeSymbol);
    }

    /**
     * 验证字符串是否是数据库字段
     */
    private static final Pattern P_IS_COLUMN = Pattern.compile("^\\w\\S*[\\w\\d]*$");

    /**
     * 判断字符串是否符合数据库字段的命名
     *
     * @param str 字符串
     * @return 判断结果
     */
    public static boolean isNotColumnName(String str) {
        return !P_IS_COLUMN.matcher(str).matches();
    }

    /**
     * 获取真正的字段名
     *
     * @param column 字段名
     * @return 字段名
     */
    public static String getTargetColumn(String column) {
        if (isNotColumnName(column)) {
            return column.substring(1, column.length() - 1);
        }
        return column;
    }
}
