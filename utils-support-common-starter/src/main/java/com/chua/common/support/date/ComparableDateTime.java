package com.chua.common.support.date;

import java.text.ParseException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chua.common.support.date.DateUtils.DEFAULT_ZONE_ID;


/**
 * 可写的时间
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/16
 */
public interface ComparableDateTime extends ReadableDateTime {
    /**
     * 获取最接近的时间
     *
     * @param localTimes 时间
     * @return LocalTime
     */
    default LocalTime nearLocalTime(LocalTime... localTimes) {
        return DateUtils.nearLocalTime(toLocalTime(), localTimes);
    }

    /**
     * 获取最接近的时间
     *
     * @param localDateTimes 时间
     * @return LocalDateTime
     */
    default LocalDateTime nearLocalTime(LocalDateTime... localDateTimes) {
        return DateUtils.nearLocalDateTime(toLocalDateTime(), localDateTimes);
    }

    /**
     * 添加纳秒
     *
     * @param nanos 纳秒
     * @return this
     */
    ComparableDateTime plusNanos(long nanos);

    /**
     * 添加毫秒
     *
     * @param millis 毫秒
     * @return this
     */
    ComparableDateTime plusMillis(long millis);

    /**
     * 前几毫秒
     *
     * @param millis 毫秒
     * @return this
     */
    default ComparableDateTime minusMillis(long millis) {
        return plusMillis(millis * -1L);
    }

    /**
     * 添加秒
     *
     * @param second 秒
     * @return this
     */
    ComparableDateTime plusSecond(long second);

    /**
     * 前几秒
     *
     * @param second 秒
     * @return this
     */
    default ComparableDateTime minusSecond(long second) {
        return plusSecond(second * -1L);
    }

    /**
     * 添加分
     *
     * @param minute 分
     * @return this
     */
    ComparableDateTime plusMinutes(long minute);

    /**
     * 前几分
     *
     * @param minute 小时
     * @return this
     */
    default ComparableDateTime minusMinutes(long minute) {
        return plusMinutes(minute * -1L);
    }

    /**
     * 添加小时
     *
     * @param hours 小时
     * @return this
     */
    ComparableDateTime plusHours(long hours);

    /**
     * 前几小时
     *
     * @param hours 小时
     * @return this
     */
    default ComparableDateTime minusHours(long hours) {
        return plusHours(hours * -1L);
    }

    /**
     * 添加天
     *
     * @param days 天
     * @return this
     */
    ComparableDateTime plusDays(long days);

    /**
     * 前几天
     *
     * @param days 天
     * @return this
     */
    default ComparableDateTime minusDays(long days) {
        return plusDays(days * -1L);
    }

    /**
     * 添加周
     *
     * @param weeks 周
     * @return this
     */
    ComparableDateTime plusWeeks(long weeks);

    /**
     * 前几周
     *
     * @param weeks 周
     * @return this
     */
    default ComparableDateTime minusWeeks(long weeks) {
        return plusWeeks(weeks * -1L);
    }

    /**
     * 添加月
     *
     * @param months 月
     * @return this
     */
    ComparableDateTime plusMonths(long months);

    /**
     * 前几月
     *
     * @param months 月
     * @return this
     */
    default ComparableDateTime minusMonths(long months) {
        return plusMonths(months * -1L);
    }

    /**
     * 添加年
     *
     * @param years 年
     * @return this
     */
    ComparableDateTime plusYears(long years);

    /**
     * 前几年
     *
     * @param years 年
     * @return this
     */
    default ComparableDateTime minusYears(long years) {
        return plusYears(years * -1L);
    }

    /**
     * 设置纳秒
     *
     * @param nanoOfSecond 纳秒
     * @return this
     */
    ComparableDateTime withNano(int nanoOfSecond);

    /**
     * 设置微秒
     *
     * @param millis 微秒
     * @return this
     */
    ComparableDateTime withMillis(int millis);

    /**
     * 设置秒
     *
     * @param second 秒
     * @return this
     */
    ComparableDateTime withSecond(int second);

    /**
     * 设置分
     *
     * @param minute 分
     * @return this
     */
    ComparableDateTime withMinute(int minute);

    /**
     * 设置小时
     *
     * @param hour 小时
     * @return this
     */
    ComparableDateTime withHour(int hour);

    /**
     * 昨天
     *
     * @return this
     */
    default ComparableDateTime withYesterday() {
        return plusDays(-1);
    }

    /**
     * 昨天
     *
     * @return this
     */
    default ComparableDateTime withTomorrow() {
        return plusDays(1);
    }

    /**
     * 设置00:00:00
     *
     * @param hour 小时(00:00:00)
     * @return this
     */
    default ComparableDateTime withZeroHour(int hour) {
        return withHour(0).withZeroMinute();
    }

    /**
     * 设置00:00
     *
     * @return this
     */
    default ComparableDateTime withZeroMinute() {
        return withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 设置每周的几号
     *
     * @param dayOfWeek 每周的几号
     * @return this
     */
    ComparableDateTime withDayOfWeek(int dayOfWeek);

    /**
     * 设置日期
     *
     * @param localDate 时间. e.g 2021-05-12
     * @return this
     */
    default ComparableDateTime withDate(LocalDate localDate) {
        return withYear(localDate.getYear())
                .withMonth(localDate.getMonthValue())
                .withDayOfMonth(localDate.getDayOfMonth());
    }

    /**
     * 设置日期
     *
     * @param localDate 时间. e.g 2021-05-12
     * @return this
     */
    default ComparableDateTime withDate(LocalDateTime localDate) {
        return withDate(localDate.toLocalDate());
    }

    /**
     * 设置日期
     *
     * @param localDate 时间. e.g 2021-05-12
     * @return this
     */
    default ComparableDateTime withDate(Date localDate) {
        return withDate(DateUtils.toLocalDate(localDate));
    }

    /**
     * 设置时间
     *
     * @param date 时间. e.g 2021-05-21
     * @return this
     */
    default ComparableDateTime withDate(String date) {
        try {
            return withDate(DateUtils.toLocalDate(date));
        } catch (ParseException ignored) {
        }
        return this;
    }

    /**
     * 设置时间
     *
     * @param epochMilli 时间. e.g 毫秒
     * @return this
     */
    default ComparableDateTime withDate(long epochMilli) {
        return withDate(DateUtils.toLocalDate(epochMilli));
    }

    /**
     * 设置时间
     *
     * @param time 时间. e.g 12:30:30
     * @return this
     */
    default ComparableDateTime withTime(String time) {
        try {
            LocalTime localTime = DateUtils.toLocalTime(time);
            return withTime(localTime);
        } catch (ParseException ignored) {
        }
        return this;
    }

    /**
     * 设置时间
     *
     * @param localTime 时间. e.g 12:30:30
     * @return this
     */
    default ComparableDateTime withTime(LocalTime localTime) {
        return withHour(localTime.getHour()).withMinute(localTime.getMinute()).withSecond(localTime.getSecond());
    }

    /**
     * 设置时间
     *
     * @param localTime 时间. e.g 12:30:30
     * @return this
     */
    default ComparableDateTime withTime(LocalDateTime localTime) {
        return withTime(localTime.toLocalTime());
    }

    /**
     * 设置时间
     *
     * @param epochMilli 时间. e.g 毫秒
     * @return this
     */
    default ComparableDateTime withTime(long epochMilli) {
        return withTime(DateUtils.toLocalTime(epochMilli));
    }

    /**
     * 设置时间
     *
     * @param localTime 时间. Date
     * @return this
     * @see Date
     */
    default ComparableDateTime withTime(Date localTime) {
        return withTime(DateUtils.toLocalTime(localTime));
    }

    /**
     * 设置每月的几号
     *
     * @param dayOfMonth 每月的几号
     * @return this
     */
    ComparableDateTime withDayOfMonth(int dayOfMonth);

    /**
     * 设置月
     *
     * @param month 月
     * @return this
     */
    ComparableDateTime withMonth(int month);

    /**
     * 设置年
     *
     * @param year 年
     * @return this
     */
    ComparableDateTime withYear(int year);

    //---------------------------------------组合-------------------------------------

    /**
     * 第一天开始
     *
     * @return this
     */
    default ComparableDateTime withFirstDayOfMonth() {
        return withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withMillis(0)
                .withNano(0);
    }

    /**
     * 第一天开始
     *
     * @return this
     */
    default ComparableDateTime withLastDayOfMonth() {
        return plusMonths(1)
                .withDayOfMonth(1)
                .plusDays(-1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59);
    }

    /**
     * 第一天开始
     *
     * @return this
     */
    default ComparableDateTime withFirstTimeOfDay() {
        return withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withMillis(0)
                .withNano(0);
    }

    /**
     * 第一天结束
     *
     * @return this
     */
    default ComparableDateTime withLastTimeOfDay() {
        return withHour(23)
                .withMinute(59)
                .withSecond(59);
    }
    //-------------------------------------------比较----------------------------------------------

    /**
     * 是否在{date} 之后
     *
     * @param date date
     * @return 在{date} 之后返回true
     */
    default boolean isAfter(Date date) {
        return isAfter(date, DEFAULT_ZONE_ID);
    }

    /**
     * 是否在{date} 之后
     *
     * @param date   date
     * @param zoneId 时区
     * @return 在{date} 之后返回true
     */
    default boolean isAfter(Date date, ZoneId zoneId) {
        return toLocalDateTime().isAfter(DateUtils.toLocalDateTime(date, zoneId));
    }

    /**
     * 是否在{date} 之前
     *
     * @param date date
     * @return 在{date} 之后返回true
     */
    default boolean isBefore(Date date) {
        return isBefore(date, DEFAULT_ZONE_ID);
    }

    /**
     * 是否在{date} 之前
     *
     * @param date   date
     * @param zoneId 时区
     * @return 在{date} 之前返回true
     */
    default boolean isBefore(Date date, ZoneId zoneId) {
        return toLocalDateTime().isBefore(DateUtils.toLocalDateTime(date, zoneId));

    }

    /**
     * 是否与{date}相等
     *
     * @param date date
     * @return 在{date} 之后返回true
     */
    default boolean isEquals(Date date) {
        return isEquals(date, DEFAULT_ZONE_ID);
    }


    /**
     * 是否与{date}相等
     *
     * @param date   date
     * @param zoneId 时区
     * @return 与{date}相等 返回true
     */
    default boolean isEquals(Date date, ZoneId zoneId) {
        return toLocalDateTime().isEqual(DateUtils.toLocalDateTime(date, zoneId));
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeOfMinAfter(LocalTime localTime) {
        return toLocalTime()
                .withSecond(0).withNano(0)
                .isAfter(localTime.withSecond(0).withNano(0));
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeOfMinAfter(String localTime) {
        try {
            return toLocalTime().withSecond(0).withNano(0).isAfter(DateUtils.toLocalTime(localTime).withSecond(0).withNano(0));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeOfMinBefore(LocalTime localTime) {
        return toLocalTime()
                .withSecond(0).withNano(0)
                .isBefore(localTime.withSecond(0).withNano(0));
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeOfMinBefore(String localTime) {
        try {
            return toLocalTime().withSecond(0).withNano(0).isBefore(DateUtils.toLocalTime(localTime).withSecond(0).withNano(0));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeOfMinEquals(String localTime) {
        try {
            return toLocalTime().withSecond(0).withNano(0).compareTo(DateUtils.toLocalTime(localTime).withSecond(0).withNano(0)) == 0;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeOfMinEquals(LocalTime localTime) {
        return toLocalTime().withSecond(0).withNano(0).compareTo(localTime.withSecond(0).withNano(0)) == 0;
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeEquals(LocalTime localTime) {
        return toLocalTime().compareTo(localTime) == 0;
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeEquals(String localTime) {
        try {
            return toLocalTime().compareTo(DateUtils.toLocalTime(localTime)) == 0;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeAfter(LocalTime localTime) {
        return toLocalTime()
                .isAfter(localTime);
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeAfter(String localTime) {
        try {
            return toLocalTime().isAfter(DateUtils.toLocalTime(localTime));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeBefore(LocalTime localTime) {
        return toLocalTime()
                .isBefore(localTime);
    }

    /**
     * 是否与{date}相等
     *
     * @param localTime localTime
     * @return 在{date} 之后返回true
     */
    default boolean isTimeBefore(String localTime) {
        try {
            return toLocalTime().isBefore(DateUtils.toLocalTime(localTime));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否与{date}相等
     *
     * @param localDate localTime
     * @return 在{date} 之后返回true
     */
    default boolean isDayEquals(String localDate) {
        try {
            return toLocalDate().compareTo(DateUtils.toLocalDate(localDate)) == 0;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否与{date}相等
     *
     * @param localDate localDate
     * @return 在{date} 之后返回true
     */
    default boolean isDayEquals(LocalDate localDate) {
        return toLocalDate().compareTo(localDate) == 0;
    }

    /**
     * 是否与{date}相等
     *
     * @param localDate localTime
     * @return 在{date} 之后返回true
     */
    default boolean isDayAfter(LocalDate localDate) {
        return toLocalDate()
                .isAfter(localDate);
    }

    /**
     * 是否与{date}相等
     *
     * @param localDate localTime
     * @return 在{date} 之后返回true
     */
    default boolean isDayAfter(String localDate) {
        try {
            return toLocalDate().isAfter(DateUtils.toLocalDate(localDate));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否与{date}相等
     *
     * @param localDate localTime
     * @return 在{date} 之后返回true
     */
    default boolean isDayBefore(LocalDate localDate) {
        return toLocalDate()
                .isBefore(localDate);
    }

    /**
     * 是否与{date}相等
     *
     * @param localDate localTime
     * @return 在{date} 之后返回true
     */
    default boolean isDayBefore(String localDate) {
        try {
            return toLocalDate().isBefore(DateUtils.toLocalDate(localDate));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 两个时间之间的天数
     *
     * @param source 时间
     * @param target 时间
     * @return 两个时间之间的天数
     */
    default long betweenOfDay(Date source, Date target) {
        LocalDate sourceLocalDate = DateUtils.toLocalDate(source);
        LocalDate targetLocalDate = DateUtils.toLocalDate(target);
        return Math.abs(ChronoUnit.DAYS.between(sourceLocalDate, targetLocalDate));
    }

    /**
     * 两个时间之间的天数
     *
     * @param date 时间
     * @return 两个时间之间的天数
     */
    default long betweenOfDay(Date date) {
        return betweenOfDay(toDate(), date);
    }

    /**
     * 两个时间之间的天数
     *
     * @param localDate 时间
     * @return 两个时间之间的天数
     */
    default long betweenOfDay(LocalDate localDate) {
        return betweenOfDay(toDate(), DateUtils.toDate(localDate));
    }

    /**
     * 两个时间之间的天数
     *
     * @param localDateTime 时间
     * @return 两个时间之间的天数
     */
    default long betweenOfDay(LocalDateTime localDateTime) {
        return betweenOfDay(toDate(), DateUtils.toDate(localDateTime));
    }

    /**
     * 两个时间之间的天数
     *
     * @param dateTime 时间
     * @return 两个时间之间的天数
     */
    default long betweenOfDay(ComparableDateTime dateTime) {
        LocalDate sourceLocalDate = dateTime.toLocalDate();
        LocalDate targetLocalDate = toLocalDate();
        return Math.abs(ChronoUnit.DAYS.between(sourceLocalDate, targetLocalDate));
    }

    /**
     * 两个时间之间的偏移
     *
     * @param dateTime  时间
     * @param deviation 偏移
     * @return 两个时间之间的天数
     */
    default boolean betweenOfDay(ComparableDateTime dateTime, long deviation) {
        return betweenOfDay(dateTime) < deviation;
    }

    /**
     * 两个时间之间的小时
     *
     * @param dateTime 时间
     * @return 两个时间之间的天数
     */
    default long betweenOfHours(Temporal dateTime) {
        LocalTime sourceLocalDate = LocalTime.of(0, 0, 0);
        if (dateTime instanceof LocalDate) {
            sourceLocalDate = LocalTime.of(0, 0, 0);
        } else if (dateTime instanceof LocalTime) {
            sourceLocalDate = (LocalTime) dateTime;
        } else if (dateTime instanceof LocalDateTime) {
            sourceLocalDate = ((LocalDateTime) dateTime).toLocalTime();
        }

        LocalTime targetLocalDate = toLocalTime();
        return Math.abs(ChronoUnit.HOURS.between(sourceLocalDate, targetLocalDate));
    }

    /**
     * 两个时间之间的偏移
     *
     * @param dateTime  时间
     * @param deviation 偏移
     * @return 两个时间之间的天数
     */
    default boolean betweenOfHours(Temporal dateTime, long deviation) {
        return betweenOfHours(dateTime) < deviation;
    }

    /**
     * 两个时间之间的分
     *
     * @param dateTime 时间
     * @return 两个时间之间的天数
     */
    default long betweenOfMinutes(Temporal dateTime) {
        LocalTime sourceLocalDate = LocalTime.of(0, 0, 0);
        if (dateTime instanceof LocalDate) {
            sourceLocalDate = LocalTime.of(0, 0, 0);
        } else if (dateTime instanceof LocalTime) {
            sourceLocalDate = (LocalTime) dateTime;
        } else if (dateTime instanceof LocalDateTime) {
            sourceLocalDate = ((LocalDateTime) dateTime).toLocalTime();
        }

        LocalTime targetLocalDate = toLocalTime();
        return Math.abs(ChronoUnit.MINUTES.between(sourceLocalDate, targetLocalDate));
    }

    /**
     * 两个时间之间的偏移
     *
     * @param dateTime  时间
     * @param deviation 偏移
     * @return 两个时间之间的天数
     */
    default boolean betweenOfMinutes(Temporal dateTime, long deviation) {
        return betweenOfMinutes(dateTime) < deviation;
    }

    /**
     * 两个时间之间的秒
     *
     * @param dateTime 时间
     * @return 两个时间之间的天数
     */
    default long betweenOfSeconds(Temporal dateTime) {
        LocalTime sourceLocalDate = LocalTime.of(0, 0, 0);
        if (dateTime instanceof LocalDate) {
            sourceLocalDate = LocalTime.of(0, 0, 0);
        } else if (dateTime instanceof LocalTime) {
            sourceLocalDate = (LocalTime) dateTime;
        } else if (dateTime instanceof LocalDateTime) {
            sourceLocalDate = ((LocalDateTime) dateTime).toLocalTime();
        }

        LocalTime targetLocalDate = toLocalTime();
        return Math.abs(ChronoUnit.SECONDS.between(sourceLocalDate, targetLocalDate));
    }

    /**
     * 两个时间之间的偏移
     *
     * @param dateTime  时间
     * @param deviation 偏移
     * @return 两个时间之间的天数
     */
    default boolean betweenOfSeconds(Temporal dateTime, long deviation) {
        return betweenOfSeconds(dateTime) < deviation;
    }

    /**
     * 两个时间之间的毫秒
     *
     * @param dateTime 时间
     * @return 两个时间之间的天数
     */
    default long betweenOfMillis(ComparableDateTime dateTime) {
        LocalDate sourceLocalDate = dateTime.toLocalDate();
        LocalDate targetLocalDate = toLocalDate();
        return Math.abs(ChronoUnit.MILLIS.between(sourceLocalDate, targetLocalDate));
    }

    /**
     * 两个时间之间的偏移
     *
     * @param dateTime  时间
     * @param deviation 偏移
     * @return 两个时间之间的天数
     */
    default boolean betweenOfMillis(ComparableDateTime dateTime, long deviation) {
        return betweenOfMillis(dateTime) < deviation;
    }

    /**
     * 两个时间之间的天数
     *
     * @param dateTime 时间
     * @return 两个时间之间的天数
     */
    default List<LocalDate> betweenOfLocalDate(ComparableDateTime dateTime) {
        LocalDate sourceLocalDate = dateTime.toLocalDate();
        LocalDate targetLocalDate = toLocalDate();
        LocalDate beforeDate = sourceLocalDate.isBefore(targetLocalDate) ? sourceLocalDate : targetLocalDate;
        return Stream.iterate(beforeDate, d -> d.plusDays(1)).limit(Math.abs(ChronoUnit.DAYS.between(sourceLocalDate, targetLocalDate)) + 1).collect(Collectors.toList());
    }

    /**
     * 两个时间之间的天数
     *
     * @param dateTime 时间
     * @return 两个时间之间的天数
     */
    default List<Date> betweenOfDate(ComparableDateTime dateTime) {
        return betweenOfLocalDate(dateTime).stream().map(DateUtils::toDate).collect(Collectors.toList());
    }

    /**
     * 两个时间之间的天数
     *
     * @param dateTime 时间
     * @return 两个时间之间的天数
     */
    default List<Date> betweenDate(ComparableDateTime dateTime) {
        return betweenOfLocalDate(dateTime).stream().map(DateUtils::toDate).collect(Collectors.toList());
    }

    /**
     * 两个时间之间的时间
     *
     * @param dateTime   日期
     * @param localTimes 时间
     * @return 两个时间之间的天数
     */
    default List<LocalDateTime> betweenOfLocalDateTime(ComparableDateTime dateTime, String... localTimes) {
        return betweenOfLocalDateTime(dateTime, Arrays.stream(localTimes).map(it -> {
            try {
                return DateUtils.toLocalTime(it);
            } catch (ParseException ignored) {
            }
            return null;
        }).filter(Objects::nonNull).toArray(LocalTime[]::new));
    }

    /**
     * 两个时间之间的时间
     *
     * @param dateTime   日期
     * @param localTimes 时间
     * @return 两个时间之间的天数
     */
    default List<LocalDateTime> betweenOfLocalDateTime(ComparableDateTime dateTime, LocalTime... localTimes) {
        LocalDate sourceLocalDate = dateTime.toLocalDate();
        LocalDate targetLocalDate = toLocalDate();
        LocalDate beforeDate = sourceLocalDate.isBefore(targetLocalDate) ? sourceLocalDate : targetLocalDate;
        return Stream.iterate(beforeDate, d -> d.plusDays(1))
                .limit(Math.abs(ChronoUnit.DAYS.between(sourceLocalDate, targetLocalDate)) + 1)
                .map(it -> {
                    List<LocalDateTime> items = new ArrayList<>();
                    for (LocalTime item : localTimes) {
                        LocalDateTime localDateTime = DateUtils.toLocalDateTime(it, item);
                        items.add(localDateTime);
                    }
                    return items;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 时间差
     *
     * @param dateTime 比较时间
     * @return 时间差
     */
    default String duration(ComparableDateTime dateTime) {
        return duration(dateTime.toLocalDateTime());
    }

    /**
     * 时间差
     *
     * @param localDateTime 比较时间
     * @return 时间差
     */
    default String duration(LocalDateTime localDateTime) {
        Duration duration = Duration.between(this.toLocalDateTime(), localDateTime);
        String durationString = duration.toString();
        String replace = durationString.replace("PT", "");
        replace = replace.replace("H", "时");
        replace = replace.replace("M", "分");
        replace = replace.replace("S", "秒");
        return replace;
    }
}
