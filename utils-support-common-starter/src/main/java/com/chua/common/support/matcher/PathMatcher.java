package com.chua.common.support.matcher;

/**
 * 路径匹配
 *
 * @author CH
 * @since 1.0
 */
public interface PathMatcher {
    static PathMatcher INSTANCE = new AntPathMatcher();
    /**
     * 是否满足匹配标准
     *
     * @param path 路径
     * @return boolean
     */
    boolean isPattern(String path);

    /**
     * 匹配
     *
     * @param pattern 正则
     * @param path    路径
     * @return boolean
     */
    boolean match(String pattern, String path);

    /**
     * 匹配
     *
     * @param pattern 正则
     * @param path    路径
     * @return boolean
     */
    boolean matchStart(String pattern, String path);
}
