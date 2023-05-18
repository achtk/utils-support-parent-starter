package com.chua.common.support.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具
 *
 * @author CH
 * @version 1.0.0
 */
public class RegexUtil {

    /**
     * 正则匹配
     *
     * @param regex : 正则表达式
     * @param str   : 待匹配字符串
     * @return boolean
     */
    public static boolean matches(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private static final String URL_REGEX = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * url格式校验
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isUrl(String str) {
        return str != null && str.trim().length() != 0 && matches(URL_REGEX, str);
    }

}
