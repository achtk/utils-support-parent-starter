package com.chua.common.support.lang.date;


import com.chua.common.support.lang.date.lunar.LunarTime;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import static com.chua.common.support.lang.date.DateUtils.DEFAULT_ZONE_ID;


/**
 * 可计算的时间
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/17
 */
public class DateTime extends BaseDateTime implements OptionalDateTime,
        ReadableDateTime, ComparableDateTime,
        ReadableDateRange, UnitDateTime,
        Serializable, ParseableDateTime {

    private final LocalDateTime localDateTime;

    protected DateTime(LocalDateTime localDateTime) {
        localDateTime = localDateTime.withNano(0);
        this.localDateTime = localDateTime;
    }

    @Override
    public DateTime parse(String value) {
        return new DateTimeParser(value).parse();
    }

    @Override
    public ComparableDateTime plusDays(long days) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusDays(days);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime plusHours(long hours) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusHours(hours);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime plusMillis(long millis) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plus(millis, ChronoUnit.MILLIS);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime plusMinutes(long minute) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusMinutes(minute);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime plusMonths(long months) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusMonths(months);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime plusNanos(long nanos) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusNanos(nanos);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime plusSecond(long second) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusSeconds(second);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime plusWeeks(long weeks) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusWeeks(weeks);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime plusYears(long years) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.plusYears(years);
        return of(localDateTime);
    }

    @Override
    public LocalDateTime toLocalDateTime() {
        return localDateTime;
    }

    public LunarTime toLunarTime() {
        return new LunarTime(this);
    }

    @Override
    public ComparableDateTime withDayOfMonth(int dayOfMonth) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.withDayOfMonth(dayOfMonth);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime withDayOfWeek(int dayOfWeek) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.with(ChronoField.DAY_OF_WEEK, dayOfWeek);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime withHour(int hour) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.withHour(hour);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime withMillis(int millis) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.with(ChronoField.MILLI_OF_SECOND, millis);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime withMinute(int minute) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.withMinute(minute);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime withMonth(int month) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.withMonth(month);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime withNano(int nanoOfSecond) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.withNano(nanoOfSecond);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime withSecond(int second) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.withSecond(second);
        return of(localDateTime);
    }

    @Override
    public ComparableDateTime withYear(int year) {
        LocalDateTime localDateTime = toLocalDateTime();
        localDateTime = localDateTime.withYear(year);
        return of(localDateTime);
    }

    public static DateTime now() {
        return of(LocalDateTime.now());
    }

    public static DateTime nowDay() {
        return of(LocalDate.now());
    }

    public static DateTime of(Instant instant) {
        return new DateTime(LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID));
    }

    public static DateTime of(int year, int month, int dayOfMonth) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(0, 0);
        return of(date, time);
    }

    public static DateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute);
        return of(date, time);
    }

    public static DateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second);
        return of(date, time);
    }

    public static DateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second, nanoOfSecond);
        return of(date, time);
    }

    public static DateTime of(int year, int month, int dayOfMonth, int hour, int minute) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute);
        return of(date, time);
    }

    public static DateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second);
        return of(date, time);
    }

    public static DateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second, nanoOfSecond);
        return of(date, time);
    }

    public static DateTime of(Instant instant, ZoneId zone) {
        return new DateTime(LocalDateTime.ofInstant(instant, zone));
    }

    public static DateTime of(Date date) {
        return new DateTime(DateUtils.toLocalDateTime(date));
    }

    public static DateTime of(String str) {
        LocalDateTime localDateTime = null;
        try {
            localDateTime = DateUtils.toLocalDateTime(str);
        } catch (ParseException ignored) {
        }
        return new DateTime(localDateTime);
    }

    public static DateTime of(String str, String pattern) throws ParseException {
        return new DateTime(DateUtils.toLocalDateTime(str, pattern));
    }

    public static DateTime of(long epochMilli) {
        return new DateTime(DateUtils.toLocalDateTime(epochMilli));
    }

    public static DateTime of(LocalDateTime localDateTime) {
        return new DateTime(localDateTime);
    }

    public static DateTime of(LocalDate localDate) {
        return new DateTime(DateUtils.toLocalDateTime(localDate));
    }

    public static DateTime of(LocalTime localTime) {
        return new DateTime(DateUtils.toLocalDateTime(localTime));
    }

    public static DateTime of(LocalDate localDate, LocalTime localTime) {
        return new DateTime(DateUtils.toLocalDateTime(localDate, localTime));
    }

    public static DateTime ofNanos(int nanos) {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.withNano(nanos);
        return new DateTime(localDateTime);
    }

    /**
     * 初始化
     *
     * @param calendar 对象
     * @return 时间
     */
    public static DateTime of(Calendar calendar) {
        return of(DateUtils.toLocalDateTime(calendar));
    }

    /**
     * 初始化
     *
     * @param object 对象
     * @return 时间
     */
    public static DateTime of(Object object) {

        if (null == object) {
            return null;
        }

        if (object instanceof Timestamp) {
            return of(((Timestamp) object).toLocalDateTime());
        }


        if (object instanceof java.sql.Time) {
            return of((java.sql.Time) object);
        }

        if (object instanceof java.sql.Date) {
            return of((java.sql.Date) object);
        }

        if (object instanceof Date) {
            return of((Date) object);
        }

        if (object instanceof LocalDateTime) {
            return of((LocalDateTime) object);
        }

        if (object instanceof LocalDate) {
            return of((LocalDate) object);
        }

        if (object instanceof LocalTime) {
            return of((LocalTime) object);
        }

        if (object instanceof Instant) {
            return of((Instant) object);
        }

        if (object instanceof Long) {
            return ofNanos((int) object);
        }

        if (object instanceof String) {
            return of(object.toString());
        }

        if (object instanceof Calendar) {
            return of((Calendar) object);
        }

        return DateTime.now();

    }

    @Override
    public String toString() {
        return "DateTime{" +
                "localDateTime=" + localDateTime +
                '}';
    }

    /**
     * 年龄
     * @return 年龄
     */
    public int age() {
        long birthday = this.toMillis();
        long dateToCompare = System.currentTimeMillis();
        if (birthday > dateToCompare) {
            throw new IllegalArgumentException("Birthday is after dateToCompare!");
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateToCompare);

        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        final boolean isLastDayOfMonth = dayOfMonth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.setTimeInMillis(birthday);
        int age = year - cal.get(Calendar.YEAR);

        final int monthBirth = cal.get(Calendar.MONTH);
        if (month == monthBirth) {

            final int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            final boolean isLastDayOfMonthBirth = dayOfMonthBirth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            boolean b = (!isLastDayOfMonth || !isLastDayOfMonthBirth) && dayOfMonth < dayOfMonthBirth;
            if (b) {
                // 如果生日在当月，但是未达到生日当天的日期，年龄减一
                age--;
            }
        } else if (month < monthBirth) {
            // 如果当前月份未达到生日的月份，年龄计算减一
            age--;
        }

        return age;
    }
}
