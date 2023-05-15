package com.chua.common.support.date;

import com.chua.common.support.date.format.BetweenFormatter;
import com.chua.common.support.date.type.TimeOfDay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 时间
 *
 * @author CH
 * @since 2021-12-29
 */
public class Times {
    /**
     * 格式化持续时间
     *
     * @param duration 持续时间
     * @return 格式
     */
    public static BetweenFormatter betweenOfFormat(long duration) {
        return betweenOfFormat(duration, TimeOfDay.SECOND);
    }

    /**
     * 格式化持续时间
     *
     * @param duration  持续时间
     * @param timeOfDay 类型
     * @return 格式
     */
    public static BetweenFormatter betweenOfFormat(long duration, TimeOfDay timeOfDay) {
        return new BetweenFormatter(duration, timeOfDay);
    }

    /**
     * 相差几天
     *
     * @param date1 日期
     * @param date2 日期
     * @return 几天
     */
    public static int betweenOfDay(String date1, String date2) {
        return Math.abs(DateTime.of(date1).durationDays(DateTime.of(date2).toDate()));
    }

    /**
     * 相差几天
     *
     * @param date1 日期
     * @param date2 日期
     * @return 几天
     */
    public static int betweenOfDay(Date date1, Date date2) {
        return Math.abs(DateTime.of(date1).durationDays(DateTime.of(date2).toDate()));
    }

    /**
     * 相差几天
     *
     * @param date1 日期
     * @param date2 日期
     * @return 几天
     */
    public static int betweenOfDay(LocalDate date1, LocalDate date2) {
        return Math.abs(DateTime.of(date1).durationDays(DateTime.of(date2).toDate()));
    }

    /**
     * 相差几天
     *
     * @param date1 日期
     * @param date2 日期
     * @return 几天
     */
    public static int betweenOfDay(LocalDateTime date1, LocalDateTime date2) {
        return Math.abs(DateTime.of(date1).durationDays(DateTime.of(date2).toDate()));
    }
}
