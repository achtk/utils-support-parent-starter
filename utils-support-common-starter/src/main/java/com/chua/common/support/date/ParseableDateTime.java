package com.chua.common.support.date;

/**
 * 可分析的时间
 *
 * @author chenhua
 */
public interface ParseableDateTime {
    /**
     * 分析
     *
     * @param value 数据
     * @return 时间
     */
    DateTime parse(String value);
}
