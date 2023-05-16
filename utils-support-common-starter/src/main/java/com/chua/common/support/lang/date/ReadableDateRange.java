package com.chua.common.support.lang.date;

import java.text.ParseException;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 可读的日期区间
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/15
 */
public interface ReadableDateRange extends ReadableDateTime {

    //-------------------------------------------区间数据----------------------------------------------

    /**
     * 获取当前周的一周时间
     *
     * @param format 格式化
     * @return 当前周的一周时间
     */
    default List<String> asRangeWeek(String format) {
        return asRangeWeek(DateUtils.DEFAULT_ZONE_ID, format);
    }

    /**
     * 获取当前周的一周时间
     *
     * @param zoneId 时区
     * @param format 格式化
     * @return 当前周的一周时间
     */
    default List<String> asRangeWeek(ZoneId zoneId, String format) {
        return asRangeWeek(zoneId).stream().map(date -> toString(format)).collect(Collectors.toList());
    }

    /**
     * 获取日期
     *
     * @param date 结束时间
     * @return 结果
     */
    default List<CalendarTime> asWeek(DateTime date) {
        return asWeek(date.toLocalDate());
    }

    /**
     * 获取日期
     *
     * @param date 结束时间
     * @return 结果
     */
    default List<CalendarTime> asWeek(String date) {
        return asWeek(DateTime.of(date));
    }

    /**
     * 获取日期
     *
     * @param endDate 结束时间
     * @return 结果
     */
    default List<CalendarTime> asWeek(LocalDate endDate) {
        List<CalendarTime> result = new LinkedList<>();
        LocalDate start = toLocalDate();
        while (start.isBefore(endDate) || start.isEqual(endDate)) {
            LocalDate localDate = start.with(DayOfWeek.SUNDAY);
            LocalDate next = localDate.isBefore(endDate) ? localDate : endDate;
            result.add(new CalendarTime(start, next));
            start = next.plusDays(1);
        }

        return result;
    }

    /**
     * 获取当前周的一周时间
     *
     * @return 当前周的一周时间
     */
    default List<Date> asRangeWeek() {
        return asRangeWeek(DateUtils.DEFAULT_ZONE_ID);
    }

    /**
     * 获取当前周的一周时间
     *
     * @param zoneId 时区
     * @return 当前周的一周时间
     */
    default List<Date> asRangeWeek(ZoneId zoneId) {
        Date date = firstDayOfCurrentWeek();
        List<Date> result = new ArrayList<>();
        result.add(date);

        LocalDateTime localDateTime = DateUtils.toLocalDateTime(date, zoneId);
        for (int i = 1; i < DateUtils.WEEK_DAY; i++) {
            localDateTime = localDateTime.plusDays(1);
            result.add(DateUtils.toDate(localDateTime, zoneId));
        }
        return result;
    }

    /**
     * 获取当月的时间
     *
     * @return 当月的时间
     */
    default List<Date> asCurrentMonth() {
        return asRange(firstDayOfCurrentMonth(), lastDayOfCurrentMonth());
    }

    /**
     * 获取当月的时间
     *
     * @param format 格式
     * @return 当月的时间
     */
    default List<String> asCurrentMonth(String format) {
        return asRange(firstDayOfCurrentMonth(), lastDayOfCurrentMonth(), format);
    }

    /**
     * 获取当月的时间
     *
     * @param zoneId 时区
     * @return 当月的时间
     */
    default List<Date> asCurrentMonth(ZoneId zoneId) {
        return asRange(firstDayOfCurrentMonth(zoneId), lastDayOfCurrentMonth(zoneId));
    }

    /**
     * 获取当前之后{day}长度天时间
     *
     * @param length 长度
     * @return {day}长度天时间
     */
    default List<Date> afterDay(int length) {
        return asRangeDayUntil(Math.abs(length));
    }

    /**
     * 获取当前之前{day}长度天时间
     *
     * @param length 长度
     * @return {day}长度天时间
     */
    default List<Date> beforeDay(int length) {
        return asRangeDayUntil(-1 * Math.abs(length));
    }

    /**
     * 获取{day}长度天时间
     *
     * @param length 长度
     * @return {day}长度天时间
     */
    default List<Date> asRangeDayUntil(int length) {
        LocalDateTime localDateTime1 = toLocalDateTime();
        int size = Math.abs(length);
        int step = length / size;
        List<Date> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            localDateTime1 = localDateTime1.plusDays(step);
            localDateTime1 = localDateTime1.withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .with(ChronoField.MILLI_OF_SECOND, 0)
                    .withNano(0);
            result.add(DateUtils.toDate(localDateTime1, DateUtils.DEFAULT_ZONE_ID));
        }
        return result;
    }

    /**
     * 获取两个时间的之间所有时间(第一天开始)
     *
     * @param before 开始时间
     * @param after  结束时间
     * @param format 格式
     * @return 两个时间的之间所有时间
     */
    default List<String> asRange(Date before, Date after, String format) {
        return DateUtils.asRange(before, after).stream().map(date -> toString(format)).collect(Collectors.toList());
    }

    /**
     * 获取两个时间的之间所有时间(第一天开始)
     *
     * @param before 开始时间
     * @param after  结束时间
     * @return 两个时间的之间所有时间
     */
    default List<Date> asRange(Date before, Date after) {
        return DateUtils.asRange(before, after);
    }

    /**
     * 获取两个时间的之间所有时间(第一天开始)
     *
     * @param before 开始时间
     * @param after  结束时间
     * @return 两个时间的之间所有时间
     */
    default List<LocalDate> asLocalDateRange(Date before, Date after) {
        return asRange(before, after).stream().map(it -> DateUtils.toLocalDate(it)).collect(Collectors.toList());
    }

    /**
     * 获取完整的时间区间
     *
     * @param dateTimeType 时间类型
     * @param dateTimeMode 时间获取方式
     * @param point        时间点
     * @return 时间区间
     */
    default List<Date> full(DateTimeType dateTimeType, DateTimeMode dateTimeMode, int... point) {
        return dateTimeMode == DateTimeMode.POINT ? fullPoint(dateTimeType, point) : fullRange(dateTimeType, point);
    }

    /**
     * 获取完整的时间区间
     *
     * @param dateTimeType 时间类型
     * @param points       时间点
     * @return 时间区间
     */
    default List<Date> fullPoint(DateTimeType dateTimeType, int... points) {
        if (null == points) {
            return Collections.emptyList();
        }
        List<Date> result = new ArrayList<>(points.length);
        LocalDateTime now = LocalDateTime.now();
        for (int point : points) {
            LocalDateTime localDateTime = null;
            if (dateTimeType == DateTimeType.SECOND) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getSecond(), point, 0);
            }

            if (dateTimeType == DateTimeType.MINUTE) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), point, 0, 0);
            }

            if (dateTimeType == DateTimeType.HOUR) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), point, 0, 0, 0);
            }

            if (dateTimeType == DateTimeType.DAY_OF_MONTH) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), point, 0, 0, 0, 0);
            }

            if (dateTimeType == DateTimeType.DAY) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), point, 0, 0, 0, 0);
                localDateTime.withDayOfYear(point);
            }

            if (dateTimeType == DateTimeType.MONTH) {
                localDateTime = LocalDateTime.of(now.getYear(), point, 1, 0, 0, 0);
            }

            if (dateTimeType == DateTimeType.YEAR) {
                localDateTime = LocalDateTime.of(point, now.getMonth(), 1, 0, 0, 0);
            }

            result.add(DateUtils.toDate(localDateTime));
        }
        return result;
    }

    /**
     * 获取完整的时间区间
     *
     * @param dateTimeType 时间类型
     * @param point        时间点
     * @return 时间区间
     */
    default List<Date> fullRange(DateTimeType dateTimeType, int... point) {
        if (null == point) {
            return fullPoint(dateTimeType);
        }

        if (point.length == 1) {
            return fullPoint(dateTimeType, point[0]);
        }

        return fullRange(dateTimeType, point[0], point[point.length - 1]);
    }

    /**
     * 获取完整的时间区间
     *
     * @param dateTimeType 时间类型
     * @param point1       时间点
     * @param point2       时间点
     * @return 时间区间
     */
    default List<Date> fullRange(DateTimeType dateTimeType, int point1, int point2) {
        List<Date> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = point1; i <= point2; i++) {
            LocalDateTime localDateTime = null;
            if (dateTimeType == DateTimeType.SECOND) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getSecond(), i, 0);
            }

            if (dateTimeType == DateTimeType.MINUTE) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), i, 0, 0);
            }

            if (dateTimeType == DateTimeType.HOUR) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), i, 0, 0, 0);
            }

            if (dateTimeType == DateTimeType.DAY_OF_MONTH) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), i, 0, 0, 0, 0);
            }

            if (dateTimeType == DateTimeType.DAY) {
                localDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), i, 0, 0, 0, 0);
                localDateTime.withDayOfYear(i);
            }

            if (dateTimeType == DateTimeType.MONTH) {
                localDateTime = LocalDateTime.of(now.getYear(), i, 1, 0, 0, 0);
            }

            if (dateTimeType == DateTimeType.YEAR) {
                localDateTime = LocalDateTime.of(i, now.getMonth(), 1, 0, 0, 0);
            }

            result.add(DateUtils.toDate(localDateTime));
        }
        return result;
    }

    /**
     * 日期相隔天数
     *
     * @param date 时间
     * @return 相隔天数
     */
    default int durationDays(Date date) {
        return Period.between(toLocalDate(), DateUtils.toLocalDate(date)).getDays();
    }

    /**
     * 日期相隔天数
     *
     * @param date 时间
     * @return 相隔天数
     */
    default int durationDays(LocalDate date) {
        return Period.between(toLocalDate(), date).getDays();
    }

    /**
     * 日期相隔小时
     *
     * @param date 时间
     * @return 相隔小时
     */
    default long durationHours(Date date) {
        return durationHours(DateUtils.toLocalDateTime(date));
    }

    /**
     * 日期相隔小时
     *
     * @param temporal 时间
     * @return 相隔小时
     */
    default long durationHours(Temporal temporal) {
        return Duration.between(toLocalDateTime(), temporal).toHours();
    }

    /**
     * 日期相隔分钟
     *
     * @param date 时间
     * @return 相隔分钟
     */
    default long durationMinutes(Date date) {
        return durationHours(DateUtils.toLocalDateTime(date));
    }

    /**
     * 日期相隔分钟
     *
     * @param temporal 时间
     * @return 相隔分钟
     */
    default long durationMinutes(Temporal temporal) {
        return Duration.between(toLocalDateTime(), temporal).toMinutes();
    }

    /**
     * 日期相隔毫秒数
     *
     * @param date 时间
     * @return 相隔毫秒数
     */
    default long durationMillis(Date date) {
        return durationHours(DateUtils.toLocalDateTime(date));
    }

    /**
     * 日期相隔毫秒数
     *
     * @param temporal 时间
     * @return 相隔毫秒数
     */
    default long durationMillis(Temporal temporal) {
        return Duration.between(toLocalDateTime(), temporal).toMillis();
    }

    /**
     * 时间点划分区间
     *
     * @param startTime 时间点
     * @param endTime   时间点
     * @return 时间点划分区间
     */
    default boolean isRange(LocalTime startTime, LocalTime endTime) {
        LocalTime localTime = toLocalTime();
        localTime = localTime.withNano(0).withSecond(0);
        return !(startTime.compareTo(localTime) == 1 || endTime.compareTo(localTime) == -1);
    }

    /**
     * 时间点划分区间
     *
     * @param startTime 时间点
     * @param endTime   时间点
     * @return 时间点划分区间
     * @throws ParseException ParseException
     */
    default boolean isRange(String startTime, String endTime) throws ParseException {
        return isRange(DateTime.of(startTime).toLocalTime(), DateTime.of(endTime).toLocalTime());
    }

    /**
     * 获取当前一个月
     *
     * @return 获取当前一个月
     */
    default List<LocalDate> getCurrentMonth() {
        return asLocalDateRange(firstDayOfCurrentMonth(), lastDayOfCurrentMonth());
    }

}
