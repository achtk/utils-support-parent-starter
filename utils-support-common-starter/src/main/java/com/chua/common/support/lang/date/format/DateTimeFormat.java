package com.chua.common.support.lang.date.format;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间格式
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/13
 */
public interface DateTimeFormat {
    /**
     * 时区
     *
     * @param timeZone 时区
     * @return this
     */
    DateTimeFormat withTimeZone(TimeZone timeZone);

    /**
     * 时区
     *
     * @param zone 时区
     * @return this
     */
    DateTimeFormat withZone(ZoneId zone);

    /**
     * 获取时区
     *
     * @return ZoneId
     */
    ZoneId getZone();

    /**
     * 年表
     *
     * @param chronology 年表
     * @return this
     */
    DateTimeFormat withChronology(Chronology chronology);

    /**
     * Locale
     *
     * @param locale Locale
     * @return this
     */
    DateTimeFormat withLocale(Locale locale);

    /**
     * 字符串转时间
     *
     * @param timeStr 字符串
     * @return Date
     */
    LocalDateTime parse(String timeStr);

    /**
     * 字符串转时间
     *
     * @param timeStr 字符串
     * @param pattern 表达式
     * @return Date
     */
    LocalDateTime parse(String timeStr, String pattern);

    /**
     * long串转时间
     *
     * @param epochMilli 时间
     * @return Date
     */
    LocalDateTime parse(long epochMilli);

    /**
     * 日期转时间
     * <p>日期为空返回当前时间</p>
     *
     * @param date 日期
     * @return Date
     */
    default LocalDateTime parse(Date date) {
        if (null == date) {
            date = new Date();
        }
        return parse(date.getTime());
    }
}
