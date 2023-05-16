package com.chua.common.support.lang.date;


import com.chua.common.support.lang.date.constant.DateFormatConstant;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.chua.common.support.lang.date.DateUtils.DEFAULT_ZONE_ID;


/**
 * 可读日期时间
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/13
 */
public interface ReadableDateTime {

    String[] WEEK_NAME = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 获取类型数据
     *
     * @param field 类型
     * @return int
     */
    default int get(TemporalField field) {
        return toLocalDateTime().get(field);
    }

    /**
     * 获取小时
     *
     * @return 小时
     */
    default int getHour() {
        return toLocalDateTime().getHour();
    }

    /**
     * 获取分钟
     *
     * @return 分钟
     */
    default int getMinute() {
        return toLocalDateTime().getMinute();
    }

    /**
     * 获取秒
     *
     * @return 秒
     */
    default int getSecond() {
        return toLocalDateTime().getSecond();
    }

    /**
     * 获取毫秒
     *
     * @return 毫秒
     */
    default int getMilli() {
        return get(ChronoField.MILLI_OF_SECOND);
    }

    /**
     * 获取纳秒
     *
     * @return 纳秒
     */
    default int getNano() {
        return toLocalDateTime().getNano();
    }


    /**
     * 获取星期几
     *
     * @return 星期几
     */
    default int getDayOfWeek() {
        return toLocalDateTime().getDayOfWeek().getValue();
    }

    /**
     * 获取星期几
     *
     * @return 星期几
     */
    default String getDayOfWeekName() {
        return WEEK_NAME[getDayOfWeek()];
    }

    /**
     * 获取在第几个周几
     *
     * @param dayOfWeek 星期几
     * @return 第几天
     */
    default DateTime getDayOfCalendar(DayOfWeek dayOfWeek) {
        return DateTime.of(toLocalDateTime().with(dayOfWeek));
    }

    /**
     * 获取在第几个周几
     *
     * @param dayOfWeek 星期几
     * @param week      下几周
     * @return 第几天
     */
    default DateTime afterDayOfCalendar(DayOfWeek dayOfWeek, int week) {
        return DateTime.of(toLocalDateTime().plusWeeks(week).with(dayOfWeek));
    }

    /**
     * 获取在第几个周几
     *
     * @param dayOfWeek 星期几
     * @param week      上几周
     * @return 第几天
     */
    default DateTime beforeDayOfCalendar(DayOfWeek dayOfWeek, int week) {
        return DateTime.of(toLocalDateTime().minusWeeks(1).with(dayOfWeek));
    }

    /**
     * 获取在第几个周几
     *
     * @param dayOfWeek 星期几
     * @return 第几天
     */
    default DateTime afterDayOfCalendar(DayOfWeek dayOfWeek) {
        return DateTime.of(toLocalDateTime().plusWeeks(1).with(dayOfWeek));
    }

    /**
     * 获取在第几个周几
     *
     * @param dayOfWeek 星期几
     * @return 第几天
     */
    default DateTime beforeDayOfCalendar(DayOfWeek dayOfWeek) {
        return DateTime.of(toLocalDateTime().minusWeeks(1).with(dayOfWeek));
    }

    /**
     * 获取在每月的第几天
     *
     * @return 第几天
     */

    default int getDayOfMonth() {
        return toLocalDateTime().getDayOfMonth();
    }

    /**
     * 获取在每年的第几天
     *
     * @return 第几天
     */
    default int getDayOfYear() {
        return toLocalDateTime().getDayOfYear();
    }

    /**
     * 获得每年的第几月
     *
     * @return 第几月
     */
    default int getMonthOfYear() {
        return toLocalDateTime().getMonthValue();
    }

    /**
     * 获取年
     *
     * @return 年
     */
    default int getYear() {
        return toLocalDateTime().getYear();
    }

    //*********************************to********************************************

    /**
     * 作为日历
     *
     * @return Calendar
     */
    default Calendar toCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.toDate(toLocalDateTime()));
        return calendar;
    }

    /**
     * 获取{@link DateTime}秒
     *
     * @return long
     */
    default long toSecond() {
        return toSecond(DEFAULT_ZONE_ID);
    }

    /**
     * 获取{@link DateTime}秒
     *
     * @param zoneId 时区
     * @return long
     */
    default long toSecond(ZoneId zoneId) {
        return toMillis(zoneId) / 1000L;
    }

    /**
     * 获取 milliSecond
     *
     * @param zoneId 时区
     * @return date
     */
    default long toMillis(ZoneId zoneId) {
        return toLocalDateTime().atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 获取{@link DateTime}毫秒
     *
     * @return long
     */
    default long toMillis() {
        return toMillis(DEFAULT_ZONE_ID);
    }

    /**
     * 获取LocalTime
     *
     * @return date
     */
    default LocalTime toLocalTime() {
        return DateUtils.toLocalTime(toLocalDateTime());
    }

    /**
     * 获取LocalTime
     *
     * @return date
     */
    default Date toDate() {
        return DateUtils.toDate(toLocalDateTime());
    }

    /**
     * 获取 LocalDate
     *
     * @return date
     */
    default LocalDate toLocalDate() {
        return DateUtils.toLocalDate(toLocalDateTime());
    }


    /**
     * 获取{@link DateTime}LocalDateTime
     *
     * @return LocalDateTime
     */
    LocalDateTime toLocalDateTime();


    //***********************************相对日期*****************************************

    /**
     * 获取当前周的第一天(00:00:00)
     *
     * @param format 格式
     * @return 当前周的第一天
     */
    default String firstDayOfCurrentWeek(String format) {
        return DateUtils.format(firstDayOfCurrentWeek(), format);
    }

    /**
     * 获取当前周的第一天(00:00:00)
     *
     * @return 当前周的第一天
     */
    default Date firstDayOfCurrentWeek() {
        return firstDayOfCurrentWeek(DEFAULT_ZONE_ID);
    }

    /**
     * 获取当前周的第一天(00:00:00)
     *
     * @param zoneId 时区
     * @return 当前周的第一天
     */
    default Date firstDayOfCurrentWeek(ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        LocalDateTime dateTime = localDateTime
                .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                .plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .withNano(0);
        return DateUtils.toDate(dateTime, zoneId);
    }

    /**
     * 获取当前周的最后一天(23:59:59)
     *
     * @return 当前周的最后一天
     */
    default Date lastDayOfCurrentWeek() {
        return lastDayOfCurrentWeek(DEFAULT_ZONE_ID);
    }

    /**
     * 获取当前周的最后一天(23:59:59)
     *
     * @param format 格式
     * @return 当前周的最后一天
     */
    default String lastDayOfCurrentWeek(String format) {
        return DateUtils.format(lastDayOfCurrentWeek(), format);
    }

    /**
     * 获取当前周的最后一天(23:59:59)
     *
     * @param zoneId 时区
     * @return 当前周的最后一天
     */
    default Date lastDayOfCurrentWeek(ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        LocalDateTime sunday = localDateTime
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .minusDays(1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .with(ChronoField.MILLI_OF_SECOND, 999);
        return DateUtils.toDate(sunday, zoneId);
    }

    /**
     * 获取{preOrNext}周的第一天(00:00:00)
     *
     * @return 当前周的第一天
     */
    default Date firstDayOfWeek() {
        return firstDayOfWeek(0);
    }

    /**
     * 获取{preOrNext}周的第一天(00:00:00)
     *
     * @param preOrNext 前/后{preOrNext}周
     * @return 当前周的第一天
     */
    default Date firstDayOfWeek(int preOrNext) {
        return firstDayOfWeek(preOrNext, DEFAULT_ZONE_ID);
    }

    /**
     * 获取{preOrNext}周的第一天(00:00:00)
     *
     * @param preOrNext 前/后{preOrNext}周
     * @param format    格式化
     * @return 当前周的第一天
     */
    default String firstDayOfWeek(int preOrNext, String format) {
        return DateUtils.format(firstDayOfWeek(preOrNext, DEFAULT_ZONE_ID), format);
    }

    /**
     * 获取{preOrNext}周的第一天开始时间(00:00:00)
     *
     * @param preOrNext 前/后{preOrNext}周
     * @param zoneId    时区
     * @return 当前周的第一天
     */
    default Date firstDayOfWeek(int preOrNext, ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusWeeks(preOrNext);
        LocalDateTime dateTime = localDateTime
                .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                .plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .withNano(0);
        return DateUtils.toDate(dateTime, zoneId);
    }

    /**
     * 获取{preOrNext}周的最后一天的最后时间(23:59:59)
     *
     * @return 当前周的第一天
     */
    default Date lastDayOfWeek() {
        return lastDayOfWeek(0);
    }

    /**
     * 获取{preOrNext}周的最后一天的最后时间(23:59:59)
     *
     * @param preOrNext 前/后{preOrNext}周
     * @return 当前周的第一天
     */
    default Date lastDayOfWeek(int preOrNext) {
        return lastDayOfWeek(preOrNext, DEFAULT_ZONE_ID);
    }

    /**
     * 获取{preOrNext}周的最后一天的最后时间(23:59:59)
     *
     * @param preOrNext 前/后{preOrNext}周
     * @param zoneId    时区
     * @return 当前周的第一天
     */
    default Date lastDayOfWeek(int preOrNext, ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusWeeks(preOrNext);
        LocalDateTime sunday = localDateTime
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .minusDays(1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59);
        return DateUtils.toDate(sunday, zoneId);
    }

    /**
     * 获取{preOrNext}周的第一天(00:00:00)
     *
     * @param preOrNext 前/后{preOrNext}周
     * @return 当前周的第一天
     */
    default Date firstDayOfMonth(int preOrNext) {
        return firstDayOfMonth(preOrNext, DEFAULT_ZONE_ID);
    }

    /**
     * 获取{preOrNext}周的第一天(00:00:00)
     *
     * @param preOrNext 前/后{preOrNext}周
     * @param zoneId    时区
     * @return 当前周的第一天
     */
    default Date firstDayOfMonth(int preOrNext, ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusMonths(preOrNext);
        LocalDateTime sunday = localDateTime
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return DateUtils.toDate(sunday, zoneId);
    }

    /**
     * 获取{preOrNext}周的最后一天(23:59:59)
     *
     * @param preOrNext 前/后{preOrNext}周
     * @return 当前周的第一天
     */
    default Date lastDayOfMonth(int preOrNext) {
        return lastDayOfMonth(preOrNext, DEFAULT_ZONE_ID);
    }

    /**
     * 获取{preOrNext}周的最后一天(23:59:59)
     *
     * @param preOrNext 前/后{preOrNext}周
     * @param zoneId    时区
     * @return 当前周的第一天
     */
    default Date lastDayOfMonth(int preOrNext, ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusMonths(preOrNext);
        LocalDateTime sunday = localDateTime
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(23)
                .withMinute(59)
                .withSecond(59);
        return DateUtils.toDate(sunday, zoneId);
    }

    /**
     * 获取当前周的第一天(00:00:00)
     *
     * @param format 格式
     * @return 当前周的第一天
     */
    default String firstDayOfCurrentMonth(String format) {
        return DateUtils.format(firstDayOfCurrentMonth(DEFAULT_ZONE_ID), format);
    }

    /**
     * 获取当前周的第一天(00:00:00)
     *
     * @param zoneId 时区
     * @param format 格式
     * @return 当前周的第一天
     */
    default String firstDayOfCurrentMonth(ZoneId zoneId, String format) {
        return DateUtils.format(firstDayOfCurrentMonth(zoneId), format);
    }

    /**
     * 获取当前月的第一天(00:00:00)
     *
     * @return 当前周的第一天
     */
    default Date firstDayOfCurrentMonth() {
        return firstDayOfCurrentMonth(DEFAULT_ZONE_ID);
    }

    /**
     * 获取当前月的第一天(00:00:00)
     *
     * @param zoneId 时区
     * @return 当前周的第一天
     */
    default Date firstDayOfCurrentMonth(ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        LocalDateTime sunday = localDateTime
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return DateUtils.toDate(sunday, zoneId);
    }

    /**
     * 获取当前月的最后一天(23:59:59)
     *
     * @param format 格式
     * @return 当前周的最后一天
     */
    default String lastDayOfCurrentMonth(String format) {
        return DateUtils.format(lastDayOfCurrentMonth(DEFAULT_ZONE_ID), format);
    }

    /**
     * 获取当前月的最后一天(23:59:59)
     *
     * @param zoneId 时区
     * @param format 格式
     * @return 当前周的最后一天
     */
    default String lastDayOfCurrentMonth(ZoneId zoneId, String format) {
        return DateUtils.format(lastDayOfCurrentMonth(zoneId), format);
    }

    /**
     * 获取当前月的最后一天(23:59:59)
     *
     * @return 当前周的最后一天
     */
    default Date lastDayOfCurrentMonth() {
        return lastDayOfCurrentMonth(DEFAULT_ZONE_ID);
    }

    /**
     * 获取当前月的最后一天(23:59:59)
     *
     * @param zoneId 时区
     * @return 当前周的最后一天
     */
    default Date lastDayOfCurrentMonth(ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        LocalDateTime sunday = localDateTime
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59);
        return DateUtils.toDate(sunday, zoneId);
    }

    /**
     * 获取{preOrAfter}天(00:00:00)
     *
     * @param preOrAfter 天数
     * @return 当前周的最后一天
     */
    default Date firstDay(int preOrAfter) {
        return firstDay(preOrAfter, DEFAULT_ZONE_ID);
    }

    /**
     * 获取{preOrAfter}天(00:00:00)
     *
     * @param preOrAfter 天数
     * @param format     格式
     * @return 当前周的最后一天
     */
    default String firstDay(int preOrAfter, String format) {
        return DateUtils.format(firstDay(preOrAfter), format);
    }

    /**
     * 获取{preOrAfter}天(00:00:00)
     *
     * @param preOrAfter 天数
     * @param zoneId     时区
     * @param format     格式
     * @return 当前周的最后一天
     */
    default String firstDay(int preOrAfter, ZoneId zoneId, String format) {
        return DateUtils.format(firstDay(preOrAfter, zoneId), format);
    }

    /**
     * 获取{preOrAfter}天(00:00:00)
     *
     * @param preOrAfter 天数
     * @param zoneId     时区
     * @return 当前周的最后一天
     */
    default Date firstDay(int preOrAfter, ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        LocalDateTime sunday = localDateTime
                .plusDays(preOrAfter)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return DateUtils.toDate(sunday, zoneId);
    }

    /**
     * 获取{preOrAfter}天(23:59:59)
     *
     * @param preOrAfter 天数
     * @return 当前周的最后一天
     */
    default Date lastDay(int preOrAfter) {
        return lastDay(preOrAfter, DEFAULT_ZONE_ID);
    }

    /**
     * 获取{preOrAfter}天 (23:59:59)
     *
     * @param preOrAfter 天数
     * @param format     格式
     * @return 当前周的最后一天
     */
    default String lastDay(int preOrAfter, String format) {
        return DateUtils.format(lastDay(preOrAfter), format);
    }

    /**
     * 获取{preOrAfter}天(23:59:59)
     *
     * @param preOrAfter 天数
     * @param zoneId     时区
     * @param format     格式
     * @return 当前周的最后一天
     */
    default String lastDay(int preOrAfter, ZoneId zoneId, String format) {
        return DateUtils.format(lastDay(preOrAfter, zoneId), format);
    }

    /**
     * 获取{preOrAfter}天(23:59:59)
     *
     * @param preOrAfter 天数
     * @param zoneId     时区
     * @return 当前周的最后一天
     */
    default Date lastDay(int preOrAfter, ZoneId zoneId) {
        LocalDateTime localDateTime = toLocalDateTime();
        LocalDateTime sunday = localDateTime
                .plusDays(preOrAfter)
                .withHour(23)
                .withMinute(59)
                .withSecond(59);
        return DateUtils.toDate(sunday, zoneId);
    }
    //-------------------------------------------格式化----------------------------------------------

    /**
     * 格式化时间.
     *
     * @param pattern 时间格式
     * @return 时间格式
     * @throws IllegalArgumentException if the pattern is invalid
     * @throws DateTimeException        if an error occurs during formatting
     */
    default String toString(String pattern) {
        return toFormat(pattern);
    }

    /**
     * 格式化时间.
     *
     * @param pattern 时间格式
     * @return 时间格式
     * @throws IllegalArgumentException if the pattern is invalid
     * @throws DateTimeException        if an error occurs during formatting
     */
    default String toFormat(String pattern) {
        return toFormat(pattern, Locale.getDefault());
    }

    /**
     * 格式化时间.
     *
     * @param dateTimeFormatter 时间格式
     * @return 时间格式
     * @throws IllegalArgumentException if the pattern is invalid
     * @throws DateTimeException        if an error occurs during formatting
     */
    default String toFormat(DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.format(toLocalDateTime());
    }

    /**
     * 格式化时间.
     *
     * @param pattern 时间格式
     * @param locale  地区
     * @return 时间格式
     * @throws IllegalArgumentException if the pattern is invalid
     * @throws DateTimeException        if an error occurs during formatting
     */
    default String toFormat(String pattern, Locale locale) {
        return DateTimeFormatter.ofPattern(pattern, locale).format(toLocalDateTime());
    }

    /**
     * unix time
     *
     * @return unix time
     */
    default long toUnixTimestamp() {
        return toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli() / 1000;
    }

    /**
     * unix time
     *
     * @return unix time
     */
    default String getStringDate() {
        return toFormat(DateFormatConstant.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 格式化时间.
     *
     * @return 时间格式
     * @throws IllegalArgumentException if the pattern is invalid
     * @throws DateTimeException        if an error occurs during formatting
     */
    default String toStandard() {
        return toString(DateFormatConstant.YYYY_MM_DD_HH_MM_SS);
    }
}
