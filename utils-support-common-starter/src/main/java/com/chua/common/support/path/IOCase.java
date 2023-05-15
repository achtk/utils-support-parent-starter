package com.chua.common.support.path;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

/**
 * IOCase
 *
 * @author CH
 */
@Getter
@AllArgsConstructor
public enum IOCase {
    /**
     *
     */
    SENSITIVE("Sensitive", true),
    /**
     *
     */
    INSENSITIVE("Insensitive", false),
    /**
     *
     */
    SYSTEM("System", File.separatorChar != '\\');
    private final String name;

    /**
     * The sensitivity flag.
     */
    private final transient boolean sensitive;

    /**
     * 检查索引
     *
     * @param str           字符串
     * @param strStartIndex 开始位置
     * @param search        查询数据
     * @return 索引
     */
    public int checkIndexOf(final String str, final int strStartIndex, final String search) {
        final int endIndex = str.length() - search.length();
        if (endIndex >= strStartIndex) {
            for (int i = strStartIndex; i <= endIndex; i++) {
                if (checkRegionMatches(str, i, search)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 查看区域
     *
     * @param str           数据源
     * @param strStartIndex 开始位置
     * @param search        索引
     * @return 是否存在数据
     */
    public boolean checkRegionMatches(final String str, final int strStartIndex, final String search) {
        return str.regionMatches(!sensitive, strStartIndex, search, 0, search.length());
    }

    /**
     * 检查后缀
     *
     * @param str 字符串
     * @param end 结尾
     * @return 检查后缀
     */
    public boolean checkEndsWith(final String str, final String end) {
        final int endLen = end.length();
        return str.regionMatches(!sensitive, str.length() - endLen, end, 0, endLen);
    }

    /**
     * 检查前缀
     *
     * @param str   字符串
     * @param start 前缀
     * @return 检查前缀
     */
    public boolean checkStartsWith(final String str, final String start) {
        return str.regionMatches(!sensitive, 0, start, 0, start.length());
    }

    /**
     * 是否相等
     *
     * @param str1 字符串
     * @param str2 字符串
     * @return 是否相等
     */
    public boolean checkEquals(final String str1, final String str2) {
        if (str1 == null || str2 == null) {
            throw new NullPointerException("The strings must not be null");
        }
        return sensitive ? str1.equals(str2) : str1.equalsIgnoreCase(str2);
    }
}